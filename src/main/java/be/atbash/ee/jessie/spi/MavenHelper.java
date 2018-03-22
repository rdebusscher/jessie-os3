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
package be.atbash.ee.jessie.spi;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 */
@ApplicationScoped
public class MavenHelper {

    public void addDependency(Model pomFile, String groupId, String artifactId, String version) {
        addDependency(pomFile, groupId, artifactId, version, null);
    }

    public void addDependency(Model pomFile, String groupId, String artifactId, String version, String scope) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        if (scope != null) {

            dependency.setScope(scope);
        }
        pomFile.addDependency(dependency);

    }

}
