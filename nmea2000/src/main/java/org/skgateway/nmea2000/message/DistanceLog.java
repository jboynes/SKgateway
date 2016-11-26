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
package org.skgateway.nmea2000.message;

import java.nio.ByteBuffer;
import java.time.Instant;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.MessageUtil;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class DistanceLog extends Message {
    private final Instant measurementTime;
    private final Quantity<Length> log;
    private final Quantity<Length> tripLog;

    public DistanceLog(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        measurementTime = MessageUtil.dateTime(data);
        log = Measurements.length(Integer.toUnsignedLong(data.getInt()));
        tripLog = Measurements.length(Integer.toUnsignedLong(data.getInt()));
    }

    public DistanceLog(int source, int destination, int priority, Instant measurementTime, Length log, Length tripLog) {
        super(source, destination, priority);
        this.measurementTime = measurementTime;
        this.log = log;
        this.tripLog = tripLog;
    }

    @Override
    public int pgn() {
        return PGN.DISTANCE_LOG;
    }

    public Instant measurementTime() {
        return measurementTime;
    }

    public Quantity<Length> log() {
        return log;
    }

    public Quantity<Length> tripLog() {
        return tripLog;
    }

    @Override
    public String toString() {
        return "DistanceLog(" + measurementTime + ", " + log + ", " + tripLog + ")";
    }
}

