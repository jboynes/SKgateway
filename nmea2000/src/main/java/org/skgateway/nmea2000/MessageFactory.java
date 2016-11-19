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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MessageFactory {
    private interface Signature {
        Message newInstance(int source, int destination, int priority, ByteBuffer data);
    }

    private static final Map<Integer, Signature> FACTORIES;
    static {
        FACTORIES = new HashMap<>();
        FACTORIES.put(60928, ISOAddressClaim::new);
        FACTORIES.put(126992, SystemTime::new);
        FACTORIES.put(127245, Rudder::new);
        FACTORIES.put(127250, VesselHeading::new);
        FACTORIES.put(128259, Speed::new);
        FACTORIES.put(128267, WaterDepth::new);
        FACTORIES.put(128275, DistanceLog::new);
        FACTORIES.put(129025, PositionRapidUpdate::new);
        FACTORIES.put(129026, CourseRapidUpdate::new);
        FACTORIES.put(129033, TimeAndDate::new);
        FACTORIES.put(130306, WindData::new);
        FACTORIES.put(130311, EnvironmentalParameters::new);
    }

    public static Message fromData(int pgn, int source, int destination, int priority, ByteBuffer data) {
        Signature factory = FACTORIES.get(pgn);
        if (factory == null) {
            return new UnknownPGN(pgn, source, destination, priority, data);
        } else {
            return factory.newInstance(source, destination, priority, data);
        }
    }
}
