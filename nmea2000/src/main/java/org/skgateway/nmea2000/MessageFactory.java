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
package org.skgateway.nmea2000;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.skgateway.nmea2000.message.CourseRapidUpdate;
import org.skgateway.nmea2000.message.DistanceLog;
import org.skgateway.nmea2000.message.EnvironmentalParameters;
import org.skgateway.nmea2000.message.ISOAddressClaim;
import org.skgateway.nmea2000.message.PositionRapidUpdate;
import org.skgateway.nmea2000.message.Rudder;
import org.skgateway.nmea2000.message.Speed;
import org.skgateway.nmea2000.message.SystemTime;
import org.skgateway.nmea2000.message.TimeAndDate;
import org.skgateway.nmea2000.message.UnknownPGN;
import org.skgateway.nmea2000.message.VesselHeading;
import org.skgateway.nmea2000.message.WaterDepth;
import org.skgateway.nmea2000.message.WindData;

/**
 *
 */
public class MessageFactory {
    private static final Map<Integer, Signature> FACTORIES;

    static {
        FACTORIES = new HashMap<>();
        FACTORIES.put(PGN.ISO_ADDRESS_CLAIM, ISOAddressClaim::new);
        FACTORIES.put(PGN.SYSTEM_TIME, SystemTime::new);
        FACTORIES.put(PGN.RUDDER, Rudder::new);
        FACTORIES.put(PGN.VESSEL_HEADING, VesselHeading::new);
        FACTORIES.put(PGN.SPEED, Speed::new);
        FACTORIES.put(PGN.WATER_DEPTH, WaterDepth::new);
        FACTORIES.put(PGN.DISTANCE_LOG, DistanceLog::new);
        FACTORIES.put(PGN.POSITION_RAPID_UPDATE, PositionRapidUpdate::new);
        FACTORIES.put(PGN.COURSE_RAPID_UPDATE, CourseRapidUpdate::new);
        FACTORIES.put(PGN.TIME_AND_DATE, TimeAndDate::new);
        FACTORIES.put(PGN.WIND_DATA, WindData::new);
        FACTORIES.put(PGN.ENVIRONMENTAL_PARAMETERS, EnvironmentalParameters::new);
    }

    public static Message fromData(int pgn, int source, int destination, int priority, ByteBuffer data) {
        Signature factory = FACTORIES.get(pgn);
        if (factory == null) {
            return new UnknownPGN(pgn, source, destination, priority, data);
        } else {
            return factory.newInstance(source, destination, priority, data);
        }
    }

    private interface Signature {
        Message newInstance(int source, int destination, int priority, ByteBuffer data);
    }
}
