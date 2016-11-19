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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;

/**
 *
 */
public class MessageUtil {
    public static int pgn(ByteBuffer buffer) {
        return (buffer.get() & 0xff) | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff) << 16;
    }

    public static Instant dateTime(ByteBuffer data) {
        int date = data.getShort();
        int time = data.getInt();
        return Instant.ofEpochSecond(date * 24 * 60 * 60 + time / 10_000, (time % 10_000) * 100_000);
    }

    public static ByteBuffer newBuffer(int... data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i : data) {
            buffer.put((byte) i);
        }
        buffer.flip();
        return buffer;
    }
}
