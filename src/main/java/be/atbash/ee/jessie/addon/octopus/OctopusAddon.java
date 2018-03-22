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
package be.atbash.ee.jessie.addon.octopus;

import be.atbash.ee.jessie.core.artifacts.MavenCreator;
import be.atbash.ee.jessie.core.model.JavaEEVersion;
import be.atbash.ee.jessie.core.model.JessieModel;
import be.atbash.ee.jessie.core.model.ViewType;
import be.atbash.ee.jessie.spi.AbstractAddon;
import be.atbash.ee.jessie.spi.JessieAddon;
import be.atbash.ee.jessie.spi.MavenHelper;
import org.apache.maven.model.Model;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

/**
 *
 */
@ApplicationScoped
public class OctopusAddon extends AbstractAddon {

    @Inject
    private MavenHelper mavenHelper;

    @PostConstruct
    public void init() {
        defaultOptions = new HashMap<>();
        defaultOptions.put("octopus.version", "0.9.7.1");
        defaultOptions.put("deltaspike.modules", "security");
    }

    @Override
    public String addonName() {
        return "octopus";
    }

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {
        String version = options.get("version");

        String artifactId = null;
        if (model.getSpecification().getViews().contains(ViewType.JSF)) {
            if (model.getSpecification().getJavaEEVersion() != JavaEEVersion.EE6) {
                artifactId = "octopus-javaee7-jsf";
            }
            if (model.getSpecification().getJavaEEVersion() == JavaEEVersion.EE6) {
                artifactId = "octopus-javaee6-jsf";
            }
            mavenHelper.addDependency(pomFile, "be.c4j.ee.security.octopus", artifactId, version);
        }
        if (model.getSpecification().getViews().contains(ViewType.REST)) {
            throw new IllegalArgumentException("Support for Rest with Octopus");
        }
    }

    @Override
    public Set<String> alternativesNames() {
        Set<String> alternatives = new HashSet<>();
        alternatives.add("octopus");
        return alternatives;
    }

    @Override
    public List<String> getDependentAddons() {
        return Collections.singletonList("deltaspike");
    }

    @Override
    public Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons) {

        return defaultOptions;
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getParameter(JessieModel.Parameter.VARIABLES);

        String webDirectory = getWebDirectory(model);
        String webInfDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";

        processTemplateFile(webInfDirectory, "securedURLs.ini", alternatives, variables);

        processTemplateFile(webDirectory, "octopus_index.xhtml", "index.xhtml", alternatives, variables);

        processTemplateFile(webDirectory, "login.xhtml", alternatives, variables);

        String webPagesDirectory = webDirectory + "/pages";
        directoryCreator.createDirectory(webPagesDirectory);

        processTemplateFile(webPagesDirectory, "main.xhtml", alternatives, variables);

        String rootJava = getJavaApplicationRootPackage(model);
        String securityDirectory = model.getDirectory() + "/" + rootJava + "/security";
        directoryCreator.createDirectory(securityDirectory);

        processTemplateFile(securityDirectory, "ApplicationSecurityData.java", alternatives, variables);

        String resourcesDirectory = model.getDirectory() + "/src/main/resources";

        processTemplateFile(resourcesDirectory, "octopusConfig.properties", alternatives, variables);
        processTemplateFile(resourcesDirectory, "shiro_extra.ini", alternatives, variables);

    }

}
