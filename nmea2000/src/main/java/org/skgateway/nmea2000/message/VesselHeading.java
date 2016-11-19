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
import javax.measure.quantity.Angle;

import org.skgateway.nmea2000.HeadingReference;
import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;

/**
 *
 */
public class VesselHeading extends Message {
    private final int sid;
    private final Quantity<Angle> heading;
    private final Quantity<Angle> deviation;
    private final Quantity<Angle> variation;
    private final HeadingReference reference;

    public VesselHeading(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = data.get() & 0xff;
        heading = Measurements.angle(new BigDecimal(Short.toUnsignedInt(data.getShort())).movePointLeft(4));
        short val = data.getShort();
        deviation = val == 0x7fff ? null : Measurements.angle(new BigDecimal(val).movePointLeft(4));
        val = data.getShort();
        variation = val == 0x7fff ? null : Measurements.angle(new BigDecimal(val).movePointLeft(4));
        switch (data.get() & 0x3) {
        case 0:
            reference = HeadingReference.TRUE;
            break;
        case 1:
            reference = HeadingReference.MAGNETIC;
            break;
        default:
            reference = null;
        }
    }

    public VesselHeading(int source, int destination, int priority, int sid, Quantity<Angle> heading, Quantity<Angle> deviation, Quantity<Angle> variation, HeadingReference reference) {
        super(source, destination, priority);
        this.sid = sid;
        this.heading = heading;
        this.deviation = deviation;
        this.variation = variation;
        this.reference = reference;
    }

    @Override
    public int pgn() {
        return 127250;
    }

    public int sid() {
        return sid;
    }

    public Quantity<Angle> heading() {
        return heading;
    }

    public Quantity<Angle> deviation() {
        return deviation;
    }

    public Quantity<Angle> variation() {
        return variation;
    }

    public HeadingReference reference() {
        return reference;
    }

    @Override
    public String toString() {
        return "VesselHeading(" + heading + " " + reference + ", " + deviation+ ", " + variation + ")";
    }
}

