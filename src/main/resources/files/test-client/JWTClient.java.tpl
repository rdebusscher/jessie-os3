package [# th:text="${java_package}"/];

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 */

public class JWTClient {

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // TODO Use Atbash JWT Support
        PrivateKey key = readPrivateKey();

        String jwt = generateJWT(key);

        System.out.println(jwt);

        WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/data/protected");
        Response response = target.request().header("authorization", "Bearer " + jwt).buildGet().invoke();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));

    }

    private static String generateJWT(PrivateKey key) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID("theKeyId")
                .build();

        MPJWTToken token = new MPJWTToken();
        token.setAud("targetService");
        token.setIss("https://server.example.com");  // Must match the expected issues configuration values
        token.setJti(UUID.randomUUID().toString());

        token.setSub("Jessie");  // Sub is required for WildFly Swarm
        token.setUpn("Jessie");

        token.setIat(System.currentTimeMillis());
        token.setExp(System.currentTimeMillis() + 30000); // 30 Seconds expiration!

        token.addAdditionalClaims("custom-value", "Jessie specific value");

        token.setGroups(Arrays.asList("user", "protected"));

        JWSObject jwsObject = new JWSObject(header, new Payload(token.toJSONString()));

        // Apply the Signing protection
        JWSSigner signer = new RSASSASigner(key);

        try {
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return jwsObject.serialize();
    }

    /*
    public static String generateJWTString(String jsonResource) throws Exception {
        byte[] byteBuffer = new byte[16384];
        currentThread().getContextClassLoader()
                .getResource(jsonResource)
                .openStream()
                .read(byteBuffer);

        JSONParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
        JSONObject jwtJson = (JSONObject) parser.parse(byteBuffer);

        long currentTimeInSecs = (System.currentTimeMillis() / 1000);
        long expirationTime = currentTimeInSecs + 1000;

        jwtJson.put(Claims.iat.name(), currentTimeInSecs);
        jwtJson.put(Claims.auth_time.name(), currentTimeInSecs);
        jwtJson.put(Claims.exp.name(), expirationTime);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader
                .Builder(RS256)
                .keyID("/privateKey.pem")
                .type(JWT)
                .build(), parse(jwtJson));

        signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));

        return signedJWT.serialize();
    }
    */

    public static PrivateKey readPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        InputStream inputStream = JWTClient.class.getResourceAsStream("/privateKey.pem");
        String fileContent = new Scanner(inputStream).useDelimiter("\\Z").next();

        String rawKey = fileContent.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .trim();

        return KeyFactory.getInstance("RSA")
                .generatePrivate(
                    new PKCS8EncodedKeySpec(
                        Base64.getDecoder().decode(rawKey)
                    )
                );

    }

}
