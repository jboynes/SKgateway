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
package org.skgateway.server.nmea2000;

import java.util.Collections;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;
import org.skgateway.nmea2000.message.PositionRapidUpdate;
import org.skgateway.nmea2000.message.SystemTime;

/**
 *
 */
public class PgnMapper {
    private static final JsonBuilderFactory f = Json.createBuilderFactory(Collections.emptyMap());

    public static JsonObject map(Message message) {
        switch (message.pgn()) {
        case PGN.SYSTEM_TIME:
            SystemTime time = (SystemTime) message;
            return f.createObjectBuilder().add("environment", f.createObjectBuilder().add("time", f.createObjectBuilder()
                    .add("millis", time.instant().toEpochMilli())
            )).build();
        case PGN.POSITION_RAPID_UPDATE:
            PositionRapidUpdate update = (PositionRapidUpdate) message;
            return f.createObjectBuilder().add("navigation", f.createObjectBuilder().add("position", f.createObjectBuilder()
                    .add("latitude", update.position().latitude())
                    .add("longitude", update.position().longitude())
            )).build();
        default:
            return null;
        }
    }
}
