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

import java.math.BigDecimal;
import java.util.Collections;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;
import org.skgateway.nmea2000.message.PositionRapidUpdate;
import org.skgateway.nmea2000.message.SystemTime;
import org.skgateway.nmea2000.message.VesselHeading;
import org.skgateway.nmea2000.message.WindData;

/**
 *
 */
public class PgnMapper {
    private static final JsonBuilderFactory f = Json.createBuilderFactory(Collections.emptyMap());
    private final String context;

    public PgnMapper(String context) {
        this.context = context;
    }

    public JsonObject map(Message message) {
        JsonObjectBuilder values = f.createObjectBuilder();
        switch (message.pgn()) {
            case PGN.SYSTEM_TIME:
                SystemTime time = (SystemTime) message;
                values.add("environment.time.millis", time.instant().toEpochMilli());
                break;
            case PGN.POSITION_RAPID_UPDATE:
                PositionRapidUpdate update = (PositionRapidUpdate) message;
                values.add("navigation.position", f.createObjectBuilder()
                        .add("latitude", update.position().latitude())
                        .add("longitude", update.position().longitude()));
                break;
            case PGN.WIND_DATA:
                WindData windData = (WindData) message;
                switch (windData.reference()) {
                    case TRUE:
                        values.add("environment.wind.speedTrue", (BigDecimal) windData.speed().getValue());
                        break;
                    case MAGNETIC:
                        break;
                    case APPARENT:
                        values.add("environment.wind.speedApparent", (BigDecimal) windData.speed().getValue())
                                .add("environment.wind.angleApparent", (BigDecimal) windData.angle().getValue());
                        break;
                    case TRUE_TO_BOAT:
                        break;
                    case TRUE_TO_WATER:
                        break;
                }
                break;
            case PGN.VESSEL_HEADING:
                VesselHeading heading = (VesselHeading) message;
                switch (heading.reference()) {
                    case MAGNETIC:
                        values.add("navigation.headingMagnetic", (BigDecimal) heading.heading().getValue());
                        break;
                    case TRUE:
                        values.add("navigation.headingTrue", (BigDecimal) heading.heading().getValue());
                        break;
                }
                break;
            default:
                return null;
        }
        return f.createObjectBuilder()
                .add("context", context)
                .add("source", f.createObjectBuilder().add("src", message.source()).add("pgn", message.pgn()))
                .add("values", values)
                .build();
    }
}
