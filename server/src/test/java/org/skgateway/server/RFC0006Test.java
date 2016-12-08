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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.skgateway.transport.datagram.MulticastListener;

/**
 *
 */
@Ignore
public class RFC0006Test {

    private Map<List<String>, JsonObject> model = new HashMap<>();
    private Map<String, JsonObject> devices = new HashMap<>();
    private Map<List<String>, List<List<String>>> atoms = new HashMap<>();
    private Map<List<String>, List<List<String>>> associations = new HashMap<>();

    @Before
    public void init() {
        associations.put(Arrays.asList("devices", "acme-bme280-0ffbf1", "lastReading"), Collections.singletonList(Arrays.asList("vessels", "2309999", "environment", "inside", "masterCabin")));
    }

    @Test
    public void rfc0006Test() throws Exception {
        InetAddress address = InetAddress.getByName("225.4.5.6");
        NetworkInterface en1 = NetworkInterface.getByName("en1");

        // Emulate server with an N2K source
        MulticastListener server = new MulticastListener(address, en1, this::onMessage, Executors.newSingleThreadExecutor());

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    public void onMessage(JsonObject json) {
        System.out.println(json);
        if (json.containsKey("devices")) {
            onDevice(Collections.singletonList("devices"), json);
        }
    }

    public void onDevice(List<String> path, JsonObject json) {
        JsonObject device = get(json, path);
        device.forEach((deviceId, value) -> {
            List<String> devicePath = new ArrayList<>(path);
            devicePath.add(deviceId);
            JsonObject deviceData = (JsonObject) value;
            if (deviceData.containsKey("_type")) {
                onHello(devicePath, deviceData);
            }
            List<List<String>> atomPaths = atoms.get(devicePath);
            if (atomPaths == null) {
                return;
            }
            atomPaths.forEach(atomPath -> {
                JsonObject atomValue = get(json, atomPath);
                model.put(atomPath, atomValue);
            });

            onUpdate(atomPaths);
        });
    }

    private JsonObject get(JsonObject value, List<String> path) {
        for (String pathElement : path) {
            value = value.getJsonObject(pathElement);
        }
        return value;
    }

    public void onHello(List<String> path, JsonObject deviceData) {
        // As this point, an implementation would deference the _type property and find the atoms
        // We will short cut this for now.
        List<String> lastReading = new ArrayList<>(path);
        lastReading.add("lastReading");
        List<List<String>> deviceAtoms = Collections.singletonList(lastReading);
        atoms.put(path, deviceAtoms);
    }

    public void onUpdate(List<List<String>> paths) {
        paths.forEach(atomPath -> {
            JsonObject json = model.get(atomPath);
            System.out.println("Processing update of " + atomPath + ": " + json);
            List<List<String>> targets = associations.get(atomPath);
            if (targets == null) {
                return;
            }
            targets.forEach(target -> {
                System.out.println("Setting " + target + " to " + json);
                model.put(target, json);
            });
            onUpdate(targets);
        });
    }
}
