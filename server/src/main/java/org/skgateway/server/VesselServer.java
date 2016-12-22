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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.JsonObject;

/**
 *
 */
public class VesselServer {

    private Vessel self;
    private final Map<String, Vessel> vessels = new ConcurrentHashMap<>();

    public VesselServer(JsonObject root) {
        JsonObject vessels = root.getJsonObject("vessels");
        String vesselId = vessels.getString("self");
        self = new Vessel(vesselId, vessels.getJsonObject(vesselId));
        this.vessels.put(vesselId, self);
    }

    /**
     * Returns the id for this vessel.
     *
     * @return the id for this vessel
     */
    public String getVesselId() {
        return self.getVesselId();
    }

    public Vessel self() {
        return self;
    }

    public Optional<Vessel> vessel(String vesselId) {
        return Optional.ofNullable(vessels.get(vesselId));
    }

    /**
     * Process a current delta object.
     *
     * @param delta a delta object
     */
    public void onDelta(JsonObject delta) {
        delta.getJsonArray("updates").forEach(json -> {
            JsonObject update = (JsonObject) json;
            update.getJsonArray("values").forEach(json2 -> {
                JsonObject pathValue = (JsonObject) json2;
                String path = pathValue.getString("path");
                JsonObject value = pathValue.getJsonObject("value");
                if (value == null) {
                    self.removeValue(path);
                } else {
                    self.updateValue(path, value);
                }
            });
        });
    }
}
