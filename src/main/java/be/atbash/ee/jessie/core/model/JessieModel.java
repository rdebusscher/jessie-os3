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
package be.atbash.ee.jessie.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class JessieModel {

    private String directory;
    @NotNull
    @Valid
    private JessieMaven maven;

    private JessieSpecification specification;

    private String template;

    private List<String> addons = new ArrayList<>();
    private Map<String, String> options = new HashMap<>();

    @JsonIgnore
    private TechnologyStack technologyStack;

    @JsonIgnore
    private Map<String, Object> parameters = new HashMap<>();

    @JsonIgnore
    private Set<String> alternatives;  // FIXME Why was this needed?

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public JessieMaven getMaven() {
        return maven;
    }

    public void setMaven(JessieMaven maven) {
        this.maven = maven;
    }

    public JessieSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(JessieSpecification specification) {
        this.specification = specification;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getAddons() {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        return addons;
    }

    public void setAddons(List<String> addons) {
        this.addons = addons;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public TechnologyStack getTechnologyStack() {
        return technologyStack;
    }

    public void setTechnologyStack(TechnologyStack technologyStack) {
        this.technologyStack = technologyStack;
    }

    public void addParameter(Parameter parameter, Object value) {
        parameters.put(parameter.name(), value);
    }

    public <T extends Serializable> T getParameter(Parameter parameter) {
        return (T) parameters.get(parameter.name());
    }

    public enum Parameter {
        FILENAME, ALTERNATIVES, ADDONS, VARIABLES
    }

}
