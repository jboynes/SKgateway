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
public class Vessel {
    private final String vesselId;
    private final Map<String, JsonObject> state = new ConcurrentHashMap<>();

    public Vessel(String vesselId, JsonObject config) {
        this.vesselId = vesselId;
    }

    public Optional<JsonObject> value(String key) {
        return Optional.ofNullable(state.get(key));
    }

    public void updateValue(String key, JsonObject value) {
        state.put(key, value);
    }

    public void removeValue(String key) {
        state.remove(key);
    }

    public String getVesselId() {
        return vesselId;
    }
}
