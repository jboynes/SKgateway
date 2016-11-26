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

import org.skgateway.nmea2000.Message;

/**
 *
 */
public class UnknownPGN extends Message {
    private static final String hex = "0123456789ABCDEF";
    private final byte[] data;
    private int pgn;

    public UnknownPGN(int pgn, int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        this.pgn = pgn;
        this.data = new byte[data.remaining()];
        data.get(this.data);
    }

    @Override
    public int pgn() {
        return pgn;
    }

    public byte[] data() {
        return data.clone();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(64 + data.length * 3);
        buffer.append("UnknownPGN(")
                .append(source())
                .append(", ")
                .append(pgn);
        for (byte b : data) {
            buffer.append(',')
                    .append(hex.charAt((b >>> 4) & 0xf))
                    .append(hex.charAt(b & 0xf));
        }
        buffer.append(")");
        return buffer.toString();
    }
}
