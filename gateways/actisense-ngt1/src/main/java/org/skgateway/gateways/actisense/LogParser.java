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

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.MessageFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.StringTokenizer;
import java.util.function.Consumer;

/**
 * Created by jeremy on 10/15/16.
 */
public class LogParser extends Thread {
    private final BufferedReader in;
    private final Consumer<Message> consumer;
    private final ByteBuffer buffer = ByteBuffer.allocate(512).order(ByteOrder.LITTLE_ENDIAN);

    public LogParser(BufferedReader in, Consumer<Message> consumer) {
        this.in = in;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String line = in.readLine();
                if (line == null) {
                    return;
                }
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                Instant instant = Instant.parse(tokenizer.nextToken());
                int priority = Integer.parseInt(tokenizer.nextToken());
                int pgn = Integer.parseInt(tokenizer.nextToken());
                if (pgn > 0x3ffff) {
                    // ignore actisense messages
                    continue;
                }
                int source = Integer.parseInt(tokenizer.nextToken());
                int destination = Integer.parseInt(tokenizer.nextToken());
                int length = Integer.parseInt(tokenizer.nextToken());
                buffer.clear();
                while (length-- > 0) {
                    buffer.put((byte) Integer.parseInt(tokenizer.nextToken(), 16));
                }
                buffer.flip();
                Message message = MessageFactory.fromData(pgn, source, destination, priority, buffer);
                consumer.accept(message);
            } catch (IOException e) {
                return;
            }
        }
    }
}
