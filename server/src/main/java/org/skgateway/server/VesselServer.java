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

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

/**
 *
 */
public class VesselServer {
    private String vesselId;
    private JsonObject self;
    private Map<String, JsonObject> devices = new HashMap<>();

    public VesselServer(JsonObject root) {
        JsonObject vessels = root.getJsonObject("vessels");
        vesselId = vessels.getString("self");
        self = vessels.getJsonObject(vesselId);

        JsonObject manifest = self.getJsonObject("manifest");
        JsonObject devices = manifest.getJsonObject("devices");
        devices.forEach((key, value) -> this.devices.put(key, (JsonObject) value));
    }

    /**
     * Process a current delta object.
     * @param delta a delta object
     */
    public void onDelta(JsonObject delta) {
        delta.getJsonArray("updates").forEach(json -> {
            JsonObject update = (JsonObject) json;
            String path = update.getString("path");
            JsonObject value = update.getJsonObject("value");
        });
    }
}
