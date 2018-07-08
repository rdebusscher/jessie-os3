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
package be.atbash.ee.jessie.addon.primefaces;

import be.atbash.ee.jessie.core.model.JessieModel;
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
public class PrimeFacesAddon extends AbstractAddon {

    @Inject
    private MavenHelper mavenHelper;

    @PostConstruct
    public void init() {
        defaultOptions = new HashMap<>();
        defaultOptions.put("primefaces.version", "6.1");
    }

    @Override
    public String addonName() {
        return "primefaces";
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public List<String> getDependentAddons() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons) {
        return defaultOptions;
    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        Set<String> result = new HashSet<>();
        result.add("primefaces");
        return result;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {
        String version = options.get("version").getSingleValue();

        mavenHelper.addDependency(pomFile, "org.primefaces", "primefaces", version);
    }

    @Override
    public void createFiles(JessieModel model) {
        // Nothing to do
    }
}
