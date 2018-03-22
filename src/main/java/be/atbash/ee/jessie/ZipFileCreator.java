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

import be.atbash.ee.jessie.core.artifacts.FileCreator;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
public class ZipFileCreator extends FileCreator {

    private Map<String, String> archiveContent = new HashMap<>();

    @Override
    public void writeContents(String directory, String fileName, String contents) {
        archiveContent.put(directory + File.separator + fileName, contents);
    }

    public byte[] createArchive() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, String> entry : archiveContent.entrySet()) {

                ZipEntry zipEntry = new ZipEntry(entry.getKey());

                zos.putNextEntry(zipEntry);
                zos.write(entry.getValue().getBytes());
                zos.closeEntry();

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return baos.toByteArray();
    }
}
