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
package org.skgateway.services.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

/**
 *
 */
public class HttpServer {


    private final com.sun.net.httpserver.HttpServer server;

    public HttpServer() throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8080), 1);
        server.createContext("/signalk/v1/api/vessels/", this::vessels);
        server.createContext("/signalk/v1/api/vessels/self", this::self);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void self(HttpExchange exchange) {
        try (OutputStream response = exchange.getResponseBody()) {
            URI location = exchange.getRequestURI().resolve("234567890");
            exchange.getResponseHeaders().set("Location", location.getPath());
            exchange.sendResponseHeaders(301, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vessels(HttpExchange exchange) {
        try (Writer response = new OutputStreamWriter(exchange.getResponseBody())) {
            exchange.sendResponseHeaders(200, 0);
            response.write("{...}\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
