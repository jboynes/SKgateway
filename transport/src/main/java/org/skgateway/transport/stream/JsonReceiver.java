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
package org.skgateway.transport.stream;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 */
public class JsonReceiver implements Closeable {
    private final Executor executor;
    private final Consumer<JsonObject> consumer;
    private final InputStream is;

    public JsonReceiver(AsynchronousSocketChannel channel, Consumer<JsonObject> consumer, Executor executor) {
        this.consumer = consumer;
        this.executor = executor;

        is = new FilterInputStream(Channels.newInputStream(channel)) {
            @Override
            public void close() {
                // Don't let JsonReader close the underlying channel
            }
        };

        Thread reader = new Thread(this::run);
        reader.start();
    }

    private void run() {
        while (true) {
            try {
                JsonObject json = Json.createReader(is).readObject();
                executor.execute(() -> consumer.accept(json));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
