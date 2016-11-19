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

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.function.Consumer;

import org.junit.Ignore;
import org.junit.Test;

import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

@Ignore
public class SerialTest {

    @Test
    public void listDevices() {
        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers();
        while (e.hasMoreElements()) {
            CommPortIdentifier id = e.nextElement();
            System.out.println("id.getName() = " + id.getName());
        }
    }

    @Test
    public void dumpSerial() throws Exception {
        CommPortIdentifier id = CommPortIdentifier.getPortIdentifier("tty.usbserial-2C542");
        final SerialPort port = (SerialPort) id.open("", 0);
        try {
            port.setInputBufferSize(128);
            final ReadableByteChannel channel = Channels.newChannel(port.getInputStream());
            final Consumer<ByteBuffer> reader = byteBuffer -> {
                while (byteBuffer.hasRemaining()) {
                    System.out.format("%02x ", byteBuffer.get());
                }
                System.out.println();
            };
            port.addEventListener(new SerialPortEventListener() {
                private final ByteBuffer buffer = ByteBuffer.allocate(128);

                public void serialEvent(SerialPortEvent serialPortEvent) {
                    try {
                        buffer.clear();
                        channel.read(buffer);
                        buffer.flip();
                        reader.accept(buffer);
                    } catch (Exception e) {
                        synchronized (SerialTest.this) {
                            SerialTest.this.notify();
                        }
                    }
                }
            });
            port.notifyOnDataAvailable(true);
            synchronized (this) {
                this.wait();
            }
        } finally {
            port.close();
        }
    }
}
