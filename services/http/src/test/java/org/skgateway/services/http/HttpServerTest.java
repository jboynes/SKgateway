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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class HttpServerTest {

    HttpServer server;

    @Before
    public void init() throws IOException {
        server = new HttpServer();
    }

    @After
    public void after() {
        server.stop();
    }

    @Test
    public void testRedirect() throws IOException {
        URL url = new URL("http://localhost:8080/signalk/v1/api/vessels/self");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        System.out.println("connection.getResponseCode() = " + connection.getResponseCode());
    }

    @Test
    public void sleep() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

}