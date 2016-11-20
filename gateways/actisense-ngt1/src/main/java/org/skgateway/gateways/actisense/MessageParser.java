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
package org.skgateway.gateways.actisense;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.MessageFactory;
import org.skgateway.nmea2000.MessageUtil;

/**
 * Converts raw messages from the Actisense stream to NMEA2000 messages.
 */
public class MessageParser implements Consumer<ByteBuffer> {
    private static final byte N2K_MESSAGE = (byte) 0x93;
    private static final byte NGT_MESSAGE = (byte) 0xa0;

    private final Consumer<Message> sink;
    private final boolean delay;
    private int lastTimestamp;

    public MessageParser(Consumer<Message> sink, boolean delay) {
        this.sink = sink;
        this.delay = delay;
    }

    @Override
    public void accept(ByteBuffer buffer) {
        buffer.mark();
        byte checksum = 0;
        while (buffer.hasRemaining()) {
            checksum += buffer.get();
        }
        if (checksum != 0) {
            return;
        }

        buffer.rewind();
        byte type = buffer.get();
        int length = buffer.get() & 0xff;
        switch (type) {
        case NGT_MESSAGE:
            break;
        case N2K_MESSAGE:
            if (buffer.remaining() < 11) {
                return;
            }
            int priority = buffer.get() & 0xff;
            int pgn = MessageUtil.pgn(buffer);
            int destination = buffer.get() & 0xff;
            int source = buffer.get() & 0xff;
            int timestamp = buffer.getInt();
            int dataLength = buffer.get() & 0xff;
            buffer.limit(buffer.position() + dataLength);
            if (dataLength != buffer.remaining()) {
                return;
            }
            Message message = MessageFactory.fromData(pgn, source, destination, priority, buffer);

            if (delay && timestamp > lastTimestamp && lastTimestamp > 0) {
                try {
                    Thread.sleep(timestamp - lastTimestamp);
                } catch (InterruptedException e) {
                }
            }
            lastTimestamp = timestamp;
            sink.accept(message);
            break;
        }
    }
}
