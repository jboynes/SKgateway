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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Paths;

import org.skgateway.transport.JsonSupport;

/**
 *
 */
public class MulticastSender {

    public static void main(String[] args) throws Exception {

        InetAddress address = InetAddress.getByName("225.4.5.6");
        NetworkInterface en1 = NetworkInterface.getByName("en1");

        // Emulate a device sending to the multicast channel
        InetSocketAddress remote = new InetSocketAddress(address, 8375);
        DatagramChannel channel = DatagramChannel.open();
        new N2KEmulator(Paths.get("withAIS.asc"), json -> {
            try {
                System.out.println(json);
                ByteBuffer buffer = JsonSupport.marshal(json);
                channel.send(buffer, remote);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).run();
    }
}
