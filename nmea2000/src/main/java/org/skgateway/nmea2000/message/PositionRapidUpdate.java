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

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.Position;

/**
 *
 */
public class PositionRapidUpdate extends Message {
    private final Position position;

    public PositionRapidUpdate(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        BigDecimal latitude = new BigDecimal(data.getInt()).movePointLeft(7);
        BigDecimal longitude = new BigDecimal(data.getInt()).movePointLeft(7);
        position = new Position(latitude, longitude);
    }

    public PositionRapidUpdate(int source, int destination, int priority, Position position) {
        super(source, destination, priority);
        this.position = position;
    }

    @Override
    public int pgn() {
        return 129025;
    }

    public Position position() {
        return position;
    }

    @Override
    public String toString() {
        return "PositionRapidUpdate(" + position.latitude() + ", " + position.longitude() + ")";
    }
}

