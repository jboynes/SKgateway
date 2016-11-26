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

import org.skgateway.nmea2000.HeadingReference;
import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class CourseRapidUpdate extends Message {

    private final int sid;
    private final HeadingReference reference;
    private final Quantity<Angle> courseOverGround;
    private final Quantity<Speed> speedOverGround;

    public CourseRapidUpdate(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = Byte.toUnsignedInt(data.get());
        switch (data.get() & 0x3) {
        case 0:
            reference = HeadingReference.TRUE;
            break;
        case 1:
            reference = HeadingReference.MAGNETIC;
            break;
        default:
            reference = null;
            break;
        }
        courseOverGround = Measurements.angle(new BigDecimal(data.getShort()).movePointLeft(4));
        speedOverGround = Measurements.speed(new BigDecimal(data.getShort()).movePointLeft(2));
    }

    public CourseRapidUpdate(int source, int destination, int priority, int sid, HeadingReference reference, Quantity<Angle> courseOverGround, Quantity<Speed> speedOverGround) {
        super(source, destination, priority);
        this.sid = sid;
        this.reference = reference;
        this.courseOverGround = courseOverGround;
        this.speedOverGround = speedOverGround;
    }

    @Override
    public int pgn() {
        return PGN.COURSE_RAPID_UPDATE;
    }

    public HeadingReference reference() {
        return reference;
    }

    public Quantity<Angle> courseOverGround() {
        return courseOverGround;
    }

    public Quantity<Speed> speedOverGround() {
        return speedOverGround;
    }

    @Override
    public String toString() {
        return "CourseRapidUpdate(" + courseOverGround + " " + reference + ", " + speedOverGround + ")";
    }
}

