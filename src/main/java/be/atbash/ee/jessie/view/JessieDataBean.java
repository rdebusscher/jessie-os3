/*
 * Copyright 2017-2018 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.ee.jessie.view;

import be.atbash.ee.jessie.ZipFileCreator;
import be.atbash.ee.jessie.core.artifacts.Creator;
import be.atbash.ee.jessie.core.model.*;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewAccessScoped
@Named
public class JessieDataBean implements Serializable {

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    private String activeScreen;
    private JessieMaven mavenData;
    private String technologyStack;
    private String javaEEVersion;
    private String javaSEVersion;
    private String mpVersion = "1.2"; // TODO In the future we will support more versions.
    private String supportedServer;

    private TechnologyStack technologyStackType;

    private boolean deltaspikeFeature;
    private boolean primefacesFeature;
    private boolean octopusFeature;

    private boolean hasErrors;

    @PostConstruct
    public void init() {
        mavenData = new JessieMaven();
    }

    public void activePage(String activeScreen) {
        this.activeScreen = activeScreen;
        if ("build".equals(activeScreen)) {
            validateMainValues();
        }
    }

    public String selectedMenuItemStyleClass(String menuItem) {
        return activeScreen.equals(menuItem) ? "activePage" : "";
    }

    public void validateMainValues() {
        hasErrors = false;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (mavenData.getGroupId() == null || mavenData.getGroupId().trim().isEmpty()) {
            addErrorMessage(facesContext, "groupId is required");
        }

        if (mavenData.getArtifactId() == null || mavenData.getArtifactId().trim().isEmpty()) {
            addErrorMessage(facesContext, "artifactId is required");
        }

        if (technologyStackType == TechnologyStack.JAVA_EE && (javaEEVersion == null || javaEEVersion.trim().isEmpty())) {
            addErrorMessage(facesContext, "Java EE version is required");
        }

        if (javaSEVersion == null || javaSEVersion.trim().isEmpty()) {
            addErrorMessage(facesContext, "Java SE version is required");
        }

        if (technologyStackType == TechnologyStack.MP && "options".equals(activeScreen) && supportedServer.trim().isEmpty()) {
            addErrorMessage(facesContext, "Supported server selection is required");
        }
    }

    private void addErrorMessage(FacesContext facesContext, String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        hasErrors = true;
    }

    public void generateProject() {
        JessieModel model = new JessieModel();
        model.setDirectory(mavenData.getArtifactId());
        JessieMaven mavenModel = new JessieMaven();
        mavenModel.setGroupId(mavenData.getGroupId());
        mavenModel.setArtifactId(mavenData.getArtifactId());
        model.setMaven(mavenModel);

        JessieSpecification specifications = new JessieSpecification();

        if (technologyStackType == TechnologyStack.JAVA_EE) {
            specifications.setJavaEEVersion(JavaEEVersion.valueFor(javaEEVersion));
            specifications.setJavaSEVersion(JavaSEVersion.valueFor(javaSEVersion));
            specifications.setModuleStructure(ModuleStructure.SINGLE);

            List<ViewType> views = new ArrayList<>();
            views.add(ViewType.JSF);

            specifications.setViews(views);

            List<String> addons = new ArrayList<>();
            if (deltaspikeFeature) {
                addons.add("deltaspike");
            }
            if (primefacesFeature) {
                addons.add("primefaces");
            }
            if (octopusFeature) {
                addons.add("octopus");
            }
            model.setAddons(addons);
        } else {
            specifications.setJavaSEVersion(JavaSEVersion.valueFor(javaSEVersion));
            specifications.setModuleStructure(ModuleStructure.SINGLE);

            specifications.setMicroProfileVersion(MicroProfileVersion.MP12);

            model.getOptions().put("mp.server", supportedServer);
        }

        model.setSpecification(specifications);

        modelManager.prepareModel(model, false);
        creator.createArtifacts(model);

        download(zipFileCreator.createArchive());

    }

    private void download(byte[] archive) {
        String fileName = mavenData.getArtifactId() + ".zip";
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("application/zip");
        ec.setResponseContentLength(archive.length);
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try {
            OutputStream outputStream = ec.getResponseOutputStream();

            outputStream.write(archive);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); // FIXME
        }

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }

    public String getActiveScreen() {
        return activeScreen;
    }

    public JessieMaven getMavenData() {
        return mavenData;
    }

    public String getTechnologyStack() {
        return technologyStack;
    }

    public void setTechnologyStack(String technologyStack) {
        this.technologyStack = technologyStack;
        technologyStackType = TechnologyStack.valueFor(technologyStack);
        javaSEVersion = null;
        if (technologyStackType == TechnologyStack.MP) {
            javaSEVersion = "1.8";
        }
    }

    public boolean isTechnologyStackTypeJavaEE() {
        return technologyStackType == TechnologyStack.JAVA_EE;
    }

    public boolean isTechnologyStackTypeMP() {
        return technologyStackType == TechnologyStack.MP;
    }

    public String getJavaEEVersion() {
        return javaEEVersion;
    }

    public void setJavaEEVersion(String javaEEVersion) {
        this.javaEEVersion = javaEEVersion;
    }

    public String getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(String javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public String getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(String mpVersion) {
        this.mpVersion = mpVersion;
    }

    public String getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(String supportedServer) {
        this.supportedServer = supportedServer;
    }

    public boolean isDeltaspikeFeature() {
        return deltaspikeFeature;
    }

    public void setDeltaspikeFeature(boolean deltaspikeFeature) {
        this.deltaspikeFeature = deltaspikeFeature;
    }

    public boolean isPrimefacesFeature() {
        return primefacesFeature;
    }

    public void setPrimefacesFeature(boolean primefacesFeature) {
        this.primefacesFeature = primefacesFeature;
    }

    public boolean isOctopusFeature() {
        return octopusFeature;
    }

    public void setOctopusFeature(boolean octopusFeature) {
        this.octopusFeature = octopusFeature;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }
}
