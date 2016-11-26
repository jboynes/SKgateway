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
package org.skgateway.server.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 */
public class TcpServer {
    private static final JsonObject HELLO = Json.createObjectBuilder()
            .add("version", "2.0")
            .build();

    private final AsynchronousServerSocketChannel serverSocket;
    private final Set<JsonSender> clients = new CopyOnWriteArraySet<>();

    public TcpServer(InetSocketAddress address) throws IOException {
        serverSocket = AsynchronousServerSocketChannel.open().bind(address);
        serverSocket.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                serverSocket.accept(null, this);
                try {
                    System.out.println("Connection from" + result.getRemoteAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JsonSender client = new JsonSender(result);
                client.send(HELLO);
                clients.add(client);
            }

            @Override
            public void failed(Throwable thrown, Object attachment) {
                thrown.printStackTrace();
                serverSocket.accept(null, this);
            }
        });
    }

    public void sendToAll(JsonObject json) {
        clients.stream().forEach(client -> client.send(json));
    }
}
