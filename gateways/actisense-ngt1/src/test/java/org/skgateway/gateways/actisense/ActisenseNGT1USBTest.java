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

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import org.skgateway.nmea2000.message.SystemTime;

@Ignore
public class ActisenseNGT1USBTest {

    @Test
    public void canParseSimpleMessage() {
        byte[] bytes = {0x10, 0x02, 0x00, 0x01, 0x10, 0x10, 0x03, 0x10, 0x03};
        ReadableByteChannel source = Channels.newChannel(new ByteArrayInputStream(bytes));
        SerialParser parser = new SerialParser(source,
                buffer -> assertThat(buffer, equalTo(ByteBuffer.wrap(new byte[]{0x00, 0x01, 0x10, 0x03}))));
        parser.run();
    }

    @Test
    public void canConvertRawToMessage() {
        ByteBuffer buffer = ByteBuffer.allocate(128).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 0x93)
                .put((byte) 19)
                .put((byte) 0x06)
                .put((byte) 0x10).put((byte) 0xf0).put((byte) 0x01)
                .put((byte) 0xff).put((byte) 0x06)
                .putInt(0x12345678)
                .put((byte) 0x08)
                .put((byte) 0x09).put((byte)0xF0).putShort((short)0x3af1).putInt(0x0dcb3060);
        buffer.flip();
        final AtomicReference<SystemTime> ref = new AtomicReference<>();
        MessageParser parser = new MessageParser(message -> {
            assertThat(message, instanceOf(SystemTime.class));
            ref.set((SystemTime) message);

        }, false);
        parser.accept(buffer);
        SystemTime time = ref.get();
        assertThat(time.timeSource(), equalTo(SystemTime.TimeSource.GPS));
        assertThat(time.instant(), equalTo(Instant.parse("2011-04-25T06:25:42Z")));
    }

}
