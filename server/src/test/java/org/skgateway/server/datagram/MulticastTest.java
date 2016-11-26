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
package org.skgateway.server.datagram;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;

import org.skgateway.server.JsonSupport;
import org.skgateway.server.nmea2000.N2KEmulator;

/**
 *
 */
@Ignore
public class MulticastTest {

    @Test
    public void sendSomething() throws Exception {

        InetAddress address = InetAddress.getByName("225.4.5.6");
        NetworkInterface en1 = NetworkInterface.getByName("en1");

        // Emulate server with an N2K source
        MulticastListener server = new MulticastListener(address, en1, json -> System.out.println("Multicast: " + json), Executors.newSingleThreadExecutor());

        // Emulate a device sending to the multicast channel
        InetSocketAddress remote = new InetSocketAddress(address, 3858);
        DatagramChannel channel = DatagramChannel.open();
        new N2KEmulator(Paths.get("withAIS.asc"), json -> {
            try {
                ByteBuffer buffer = JsonSupport.marshal(json);
                channel.send(buffer, remote);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
