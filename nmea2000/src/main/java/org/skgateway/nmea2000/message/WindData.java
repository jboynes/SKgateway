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
import javax.measure.quantity.Speed;

import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;

/**
 *
 */
public class WindData extends Message {
    private final int sid;
    private final Quantity<Speed> speed;
    private final Quantity<Angle> angle;
    private final Reference reference;

    enum Reference {
        TRUE, MAGNETIC, APPARENT, TRUE_TO_BOAT, TRUE_TO_WATER
    }

    public WindData(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = Byte.toUnsignedInt(data.get());
        speed = Measurements.speed(new BigDecimal(Short.toUnsignedInt(data.getShort())).movePointLeft(2));
        angle = Measurements.angle(new BigDecimal(Short.toUnsignedInt(data.getShort())).movePointLeft(4));
        switch (data.get() & 0x7) {
        case 0:
            reference = Reference.TRUE;
            break;
        case 1:
            reference = Reference.MAGNETIC;
            break;
        case 2:
            reference = Reference.APPARENT;
            break;
        case 3:
            reference = Reference.TRUE_TO_BOAT;
            break;
        case 4:
            reference = Reference.TRUE_TO_WATER;
            break;
        default:
            reference = null;
            break;
        }
    }

    public WindData(int source, int destination, int priority, int sid, Quantity<Speed> speed, Quantity<Angle> angle, Reference reference) {
        super(source, destination, priority);
        this.sid = sid;
        this.speed = speed;
        this.angle = angle;
        this.reference = reference;
    }

    @Override
    public int pgn() {
        return 130306;
    }

    public int sid() {
        return sid;
    }

    public Quantity<Speed> speed() {
        return speed;
    }

    public Quantity<Angle> angle() {
        return angle;
    }

    public Reference reference() {
        return reference;
    }

    @Override
    public String toString() {
        return "WindData(" + speed + ", " + angle + ", " + reference + ")";
    }
}

