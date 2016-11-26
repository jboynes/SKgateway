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
package org.skgateway.server.nmea2000;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.json.JsonObject;

import org.skgateway.gateways.actisense.MessageParser;
import org.skgateway.gateways.actisense.SerialParser;

/**
 *
 */
public class N2KEmulator extends Thread {
    private final Path rawData;
    private final MessageParser parser;

    public N2KEmulator(Path rawData, Consumer<JsonObject> consumer) {
        this.rawData = rawData;

        parser = new MessageParser(message -> {
            if (message.source() != 160) {
                return;
            }
            JsonObject json = PgnMapper.map(message);
            if (json != null) {
                consumer.accept(json);
            }
        }, true);
    }

    @Override
    public void run() {
        try {
            try (SeekableByteChannel channel = Files.newByteChannel(rawData)) {
                SerialParser serialParser = new SerialParser(channel, parser);
                serialParser.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
