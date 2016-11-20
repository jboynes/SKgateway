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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

/**
 *
 */
public class TcpServer {

    private final AsynchronousServerSocketChannel serverSocket;
    private final Set<TcpClientConnection> clients = new CopyOnWriteArraySet<>();

    public TcpServer(int port) throws IOException {
        serverSocket = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        serverSocket.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>(){
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                try {
                    System.out.println("Connection from" + result.getRemoteAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TcpClientConnection client = new TcpClientConnection(result);
                JsonObject hello = Json.createObjectBuilder().add("version", "2.0").build();
                client.send(hello);
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

    private static class TcpClientConnection {
        private final AsynchronousSocketChannel channel;

        public TcpClientConnection(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        public void send(JsonObject json) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (JsonWriter writer = Json.createWriter(os)) {
                writer.writeObject(json);
            }
            ByteBuffer buffer = ByteBuffer.wrap(os.toByteArray());
            channel.write(buffer, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                }
            });
        }
    }
}
