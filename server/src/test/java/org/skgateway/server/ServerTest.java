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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.json.JsonObject;

import org.junit.Ignore;
import org.junit.Test;

import org.skgateway.gateways.actisense.MessageParser;
import org.skgateway.gateways.actisense.SerialParser;

/**
 *
 */
@Ignore
public class ServerTest {
    @Test
    public void runServer() throws Exception {
        int port = 3858;

        Map<Integer, PgnMapper> mappers = new HashMap<>();
        mappers.put(126992, PgnMappers::handleSystemTime);
        mappers.put(129025, PgnMappers::handlePositionRapidUpdate);
        
        TcpServer tcpServer = new TcpServer(port);
        
        MessageParser parser = new MessageParser(message -> {
            if (message.source() != 160) {
                return;
            }
            PgnMapper mapper = mappers.get(message.pgn());
            if (mapper != null) {
                JsonObject json = mapper.apply(message);
                tcpServer.sendToAll(json);
            }
        }, true);

        // Emulate an N2K source
        new Thread(() -> {
            try {
                try (SeekableByteChannel channel = Files.newByteChannel(Paths.get("withAIS.asc"))) {
                    new SerialParser(channel, parser).run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Have a simple client connect
        new Thread(new SimpleTcpClient(new InetSocketAddress(InetAddress.getLoopbackAddress(), port))).start();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    @Test
    public void dumpRawData() throws IOException {
        MessageParser parser = new MessageParser(System.out::println, true);
        try (SeekableByteChannel channel = Files.newByteChannel(Paths.get("withAIS.asc"))) {
            new SerialParser(channel, parser).run();
        }
    }
}
