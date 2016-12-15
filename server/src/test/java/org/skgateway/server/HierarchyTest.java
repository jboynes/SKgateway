/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.skgateway.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class HierarchyTest {

    private VesselServer server;
    private final Path resources = Paths.get("src/test/resources");

    @Before
    public void loadModel() throws IOException {
        server = new VesselServer(loadFile("config.json"));
    }

    @Test
    public void existingUDelta() throws IOException {
        server.onDelta(loadFile("update.json"));
    }

    private JsonObject loadFile(String s) throws IOException {
        JsonObject root;
        try (InputStream is = Files.newInputStream(resources.resolve(s))) {
            return Json.createReader(is).readObject();
        }
    }
}
