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
package org.skgateway.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

/**
 *
 */
public class JsonSupport {

    public static ByteBuffer marshal(JsonObject json) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (JsonWriter writer = Json.createWriter(os)) {
            writer.writeObject(json);
        }
        os.write(13);
        os.write(10);
        return ByteBuffer.wrap(os.toByteArray());
    }

    public static JsonObject unmarshal(ByteBuffer buffer) {
        InputStream is = new ByteBufferInputStream(buffer);
        return Json.createReader(is).readObject();
    }

    private static final class ByteBufferInputStream extends InputStream {
        private final ByteBuffer buffer;

        public ByteBufferInputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public int read() throws IOException {
            if (!buffer.hasRemaining()) {
                return -1;
            }
            return Byte.toUnsignedInt(buffer.get());
        }

        @Override
        public int read(byte[] dst, int off, int len) throws IOException {
            if (!buffer.hasRemaining()) {
                return -1;
            }

            int count = Math.min(len, buffer.remaining());
            buffer.get(dst, off, count);
            return count;
        }
    }
}
