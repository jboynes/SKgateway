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

import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class Speed extends Message {
    private final int sid;
    private final Quantity<javax.measure.quantity.Speed> waterReferenced;
    private final Quantity<javax.measure.quantity.Speed> groundReferenced;
    private final Reference reference;
    public Speed(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = Byte.toUnsignedInt(data.get());
        int val = Short.toUnsignedInt(data.getShort());
        waterReferenced = val != 0xffff ? Measurements.speed(new BigDecimal(val).movePointLeft(2)) : null;
        val = Short.toUnsignedInt(data.getShort());
        groundReferenced = val != 0xffff ? Measurements.speed(new BigDecimal(val).movePointLeft(2)) : null;
        switch (data.get() & 0xf) {
        case 0:
            reference = Reference.PADDLE_WHEEL;
            break;
        case 1:
            reference = Reference.PITOT_TUBE;
            break;
        case 2:
            reference = Reference.DOPPLER;
            break;
        case 3:
            reference = Reference.CORRELATION;
            break;
        case 4:
            reference = Reference.ELECTRO_MAGNETIC;
            break;
        default:
            reference = null;
            break;
        }
    }

    public Speed(int source, int destination, int priority, int sid, Quantity<javax.measure.quantity.Speed> waterReferenced, Quantity<javax.measure.quantity.Speed> groundReferenced, Reference reference) {
        super(source, destination, priority);
        this.sid = sid;
        this.waterReferenced = waterReferenced;
        this.groundReferenced = groundReferenced;
        this.reference = reference;
    }

    @Override
    public int pgn() {
        return PGN.SPEED;
    }

    public int sid() {
        return sid;
    }

    public Quantity<javax.measure.quantity.Speed> waterReferenced() {
        return waterReferenced;
    }

    public Quantity<javax.measure.quantity.Speed> groundReferenced() {
        return groundReferenced;
    }

    public Reference reference() {
        return reference;
    }

    @Override
    public String toString() {
        return "Speed(" + waterReferenced + ", " + groundReferenced + ", " + reference + ")";
    }

    public enum Reference {PADDLE_WHEEL, PITOT_TUBE, DOPPLER, CORRELATION, ELECTRO_MAGNETIC}
}

