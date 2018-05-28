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
package be.atbash.ee.jessie;

import be.atbash.ee.jessie.core.model.JavaEEVersion;
import be.atbash.ee.jessie.core.model.JavaSEVersion;
import be.atbash.ee.jessie.core.model.SupportedServer;
import be.atbash.ee.jessie.core.model.TechnologyStack;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Named
public class DataBean {

    private List<SelectItem> techStackItems;
    private List<SelectItem> javaEEItems;
    private List<SelectItem> javaSEItems;
    private List<SelectItem> mpItems;
    private List<SelectItem> supportedServerItems;

    @PostConstruct
    public void init() {
        defineTechStackItems();
        defineJavaEEItems();
        defineJavaSEItems();
        defineSupportedServerItems();

        mpItems = new ArrayList<>();
        mpItems.add(new SelectItem("1.2", "1.2")); // TODO In the future we will support more versions.
    }

    private void defineSupportedServerItems() {
        supportedServerItems = new ArrayList<>();
        for (SupportedServer supportedServer : SupportedServer.values()) {
            supportedServerItems.add(new SelectItem(supportedServer.getName(), supportedServer.getName()));
        }
    }

    private void defineTechStackItems() {

        techStackItems = new ArrayList<>();
        for (TechnologyStack technologyStack : TechnologyStack.values()) {
            techStackItems.add(new SelectItem(technologyStack.getCode(), technologyStack.getLabel()));
        }
    }

    private void defineJavaEEItems() {
        javaEEItems = new ArrayList<>();
        for (JavaEEVersion javaEEVersion : JavaEEVersion.values()) {
            javaEEItems.add(new SelectItem(javaEEVersion.getCode(), javaEEVersion.getLabel()));
        }
    }

    private void defineJavaSEItems() {
        javaSEItems = new ArrayList<>();
        for (JavaSEVersion javaSEVersion : JavaSEVersion.values()) {
            javaSEItems.add(new SelectItem(javaSEVersion.getCode(), javaSEVersion.getLabel()));
        }
    }

    public List<SelectItem> getJavaEEItems() {
        return javaEEItems;
    }

    public List<SelectItem> getJavaSEItems() {
        return javaSEItems;
    }

    public List<SelectItem> getTechStackItems() {
        return techStackItems;
    }

    public List<SelectItem> getMpItems() {
        return mpItems;
    }

    public List<SelectItem> getSupportedServerItems() {
        return supportedServerItems;
    }
}
