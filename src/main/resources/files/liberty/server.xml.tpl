<?xml version="1.0" encoding="UTF-8"?>
<server description="${project.artifactId}">

    <featureManager>
        <feature>microProfile-1.2</feature>
    </featureManager>

    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="${httpPort}"
                  httpsPort="${httpsPort}"/>

    <application location="${project.build.directory}/${project.build.finalName}.war"/>

    <logging traceSpecification="${log.name}.*=${log.level}"/>

    <!-- This is the keystore that will be used by SSL and by JWT.
         The keystore is built using the maven keytool plugin -->
    <keyStore id="defaultKeyStore" location="keystore.jceks" type="JCEKS" password="secret" />


    <!-- The MP JWT configuration that injects the caller's JWT into a ResourceScoped bean for inspection. -->
    <mpJwt id="jwtUserConsumer" keyName="default" audiences="simpleapp" issuer="${jwt.issuer}"/>

</server>
