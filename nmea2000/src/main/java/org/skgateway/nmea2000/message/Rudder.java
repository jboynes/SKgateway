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

import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class Rudder extends Message {
    private final int instance;
    private final int directionOrder;
    private final Quantity<Angle> angleOrder;
    private final Quantity<Angle> position;


    public Rudder(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        instance = Byte.toUnsignedInt(data.get());
        directionOrder = data.get() & 0x3;
        short val = data.getShort();
        angleOrder = val == 0x7fff ? null : Measurements.angle(new BigDecimal(val).movePointLeft(4));
        val = data.getShort();
        position = val == 0x7fff ? null : Measurements.angle(new BigDecimal(val).movePointLeft(4));
    }

    public Rudder(int source, int destination, int priority, int instance, int directionOrder, Quantity<Angle> angleOrder, Quantity<Angle> position) {
        super(source, destination, priority);
        this.instance = instance;
        this.directionOrder = directionOrder;
        this.angleOrder = angleOrder;
        this.position = position;
    }

    @Override
    public int pgn() {
        return PGN.RUDDER;
    }

    public int instance() {
        return instance;
    }

    public int directionOrder() {
        return directionOrder;
    }

    public Quantity<Angle> angleOrder() {
        return angleOrder;
    }

    public Quantity<Angle> position() {
        return position;
    }

    @Override
    public String toString() {
        return "Rudder(" + instance + ", " + directionOrder + ", " + angleOrder + ", " + position + ")";
    }
}

