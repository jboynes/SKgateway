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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 */
public class SimpleTcpClient implements Runnable {
    private final InetSocketAddress server;

    public SimpleTcpClient(InetSocketAddress server) {
        this.server = server;
    }

    @Override
    public void run() {
        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(server);
            InputStream is = Channels.newInputStream(channel);
            while (true) {
                JsonReader reader = Json.createReader(new FilterInputStream(is) {
                    @Override
                    public void close(){
                        // Don't let JsonReader close the underlying channel
                    }
                });
                JsonObject json = reader.readObject();
                System.out.println("Received: " + json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
