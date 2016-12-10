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
package org.skgateway.display;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.json.JsonObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.skgateway.transport.datagram.MulticastListener;

/**
 *
 */
public class Display extends Application {
    private final Label time = new Label();
    private final Label latitude = new Label();
    private final Label longitude = new Label();
    private DateTimeFormatter formatter;
    private MulticastListener listener;
    private final Dispatcher dispatcher;

    public Display() {
        dispatcher = new Dispatcher()
                .add("environment", new Dispatcher().add("time", json -> setTime(Instant.ofEpochMilli(json.getJsonNumber("millis").longValue()))))
        .add("navigation", new Dispatcher().add("position", json -> setPosition(json.getJsonNumber("latitude").bigDecimalValue(), json.getJsonNumber("longitude").bigDecimalValue())));
    }

    @Override
    public void init() {
        formatter = DateTimeFormatter.ofPattern("kk:mm:ss").withZone(ZoneId.systemDefault());

        try {
            InetAddress address = InetAddress.getByName("225.4.5.6");
            NetworkInterface en1 = NetworkInterface.getByName("en1");
            listener = new MulticastListener(address, en1, json -> Platform.runLater(() -> dispatcher.accept(json)), Executors.newSingleThreadExecutor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        grid.add(new Label("Time:"), 0, 0);
        grid.add(time, 1, 0);

        grid.add(new Label("Position:"), 0, 1);
        grid.add(latitude, 1, 1);
        grid.add(longitude, 1, 2);

        Scene scene = new Scene(grid, 300, 275);
        scene.getStylesheets().add("/display.css");

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
        primaryStage.show();
    }

    public void setTime(Instant instant) {
        String text = formatter.format(instant);
        System.out.println("time = " + text);
        time.setText(text);
    }

    public void setPosition(BigDecimal latitude, BigDecimal longitude) {
        String lat = formatDegrees(latitude, 'N', 'S');
        String lon = formatDegrees(longitude, 'E', 'W');
        System.out.println("lat = " + lat + ", lon = " + lon);
        this.latitude.setText(lat);
        this.longitude.setText(lon);
    }

    private static String formatDegrees(BigDecimal value, char positive, char negative) {
        char suffix = value.compareTo(BigDecimal.ZERO) < 0 ? negative : positive;
        value = value.abs();
        BigDecimal[] split = value.divideAndRemainder(BigDecimal.ONE);
        BigDecimal degrees = split[0];
        BigDecimal minutes = split[1].multiply(new BigDecimal("60"));
        return String.format("%3.0f\u00B0 %07.4f%c", degrees, minutes, suffix);
    }

    public static void main(String[] args) {
        launch(Display.class, args);
    }

    private static class Dispatcher implements Consumer<JsonObject> {
        private final Map<String, Consumer<JsonObject>> dispatchers = new HashMap<>();

        public Dispatcher add(String key, Consumer<JsonObject> consumer) {
            dispatchers.put(key, consumer);
            return this;
        }

        @Override
        public void accept(JsonObject json) {
            json.forEach((key, value) -> {
                Consumer<JsonObject> dispatcher = dispatchers.get(key);
                if (dispatcher != null) {
                    dispatcher.accept((JsonObject) value);
                }
            });
        }
    }
}
