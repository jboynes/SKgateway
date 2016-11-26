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

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class WaterDepth extends Message {
    private final int sid;
    private final Quantity<Length> depth;
    private final Quantity<Length> offset;

    public WaterDepth(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = data.get() & 0xff;
        depth = Measurements.length(new BigDecimal(data.get()).movePointLeft(2));
        offset = Measurements.length(new BigDecimal(data.get()).movePointLeft(3));
    }

    public WaterDepth(int source, int destination, int priority, int sid, Length depth, Length offset) {
        super(source, destination, priority);
        this.sid = sid;
        this.depth = depth;
        this.offset = offset;
    }

    @Override
    public int pgn() {
        return PGN.WATER_DEPTH;
    }

    public int sid() {
        return sid;
    }

    public Quantity<Length> depth() {
        return depth;
    }

    public Quantity<Length> offset() {
        return offset;
    }

    @Override
    public String toString() {
        return "WaterDepth(" + depth + ", " + offset + ")";
    }
}

