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
package be.atbash.ee.jessie.addon.deltaspike;

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
public class DeltaspikeAddon extends AbstractAddon {

    @Inject
    private MavenHelper mavenHelper;

    @PostConstruct
    public void init() {
        defaultOptions = new HashMap<>();
        defaultOptions.put("deltaspike.version", "1.8.1");
    }

    @Override
    public String addonName() {
        return "deltaspike";
    }

    @Override
    public int priority() {
        return 20;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {

        String version = options.get("version").getSingleValue();

        mavenHelper.addDependency(pomFile, "org.apache.deltaspike.core", "deltaspike-core-api", version);
        mavenHelper.addDependency(pomFile, "org.apache.deltaspike.core", "deltaspike-core-impl", version, "runtime");

        String modules = options.get("modules").getSingleValue();
        if (modules != null && !modules.isEmpty()) {
            for (String module : modules.split(",")) {
                mavenHelper.addDependency(pomFile, "org.apache.deltaspike.modules", "deltaspike-" + module.trim() + "-module-api", version);
                mavenHelper.addDependency(pomFile, "org.apache.deltaspike.modules", "deltaspike-" + module.trim() + "-module-impl", version, "runtime");
            }
        }
    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        return Collections.EMPTY_SET;
    }

    @Override
    public List<String> getDependentAddons() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons) {
        Map<String, String> options = new HashMap<>(defaultOptions);
        if (jessieModel.getSpecification().getViews().contains(ViewType.JSF)) {
            options.put("deltaspike.modules", "jsf");
        }
        return options;
    }

    @Override
    public void createFiles(JessieModel model) {
        // Nothing to do
    }
}
