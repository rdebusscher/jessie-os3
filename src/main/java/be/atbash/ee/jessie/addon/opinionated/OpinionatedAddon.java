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
package be.atbash.ee.jessie.addon.opinionated;

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
public class OpinionatedAddon extends AbstractAddon {

    @Inject
    private MavenHelper mavenHelper;

    @PostConstruct
    public void init() {
        defaultOptions = new HashMap<>();
        defaultOptions.put("octopus.version", "0.9.7.2");
    }

    @Override
    public String addonName() {
        return "opinionated";
    }

    @Override
    public int priority() {
        return 150;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {
        String version = options.get("version");

        String artifactId = null;
        if (model.getSpecification().getViews().contains(ViewType.JSF)) {
            mavenHelper.addDependency(pomFile, "be.atbash.ee.opinionated", "starter-ee7-basic", version);
        }
    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        Set<String> alternatives = new HashSet<>();
        alternatives.add("primefaces");
        alternatives.add("octopus");
        return alternatives;
    }

    @Override
    public List<String> getDependentAddons() {
        return Collections.emptyList(); // FIXME
    }

    @Override
    public Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons) {

        return Collections.emptyMap();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getParameter(JessieModel.Parameter.VARIABLES);

        String webDirectory = getWebDirectory(model);

    }

}
