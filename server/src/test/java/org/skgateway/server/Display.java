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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 */
public class Display extends Application {
    private final Text time = new Text();
    private final Text latitude = new Text();
    private final Text longitude = new Text();
    private DateTimeFormatter formatter;

    @Override
    public void init() {
        formatter = DateTimeFormatter.ofPattern("kk:mm:ss").withZone(ZoneId.systemDefault());

        ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Display.this.setTime(Instant.now());
                        Display.this.setPosition(new BigDecimal(44.0 + Math.random()), new BigDecimal(-122 - Math.random()));
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.seconds(1));
        service.start();
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
        primaryStage.setScene(scene);
        primaryStage.show();

        setTime(Instant.now());
    }

    public void setTime(Instant instant) {
        time.setText(formatter.format(instant));
    }

    public void setPosition(BigDecimal latitude, BigDecimal longitude) {
        this.latitude.setText(formatDegrees(latitude, 'N', 'S'));
        this.longitude.setText(formatDegrees(longitude, 'E', 'W'));
    }

    static String formatDegrees(BigDecimal value, char positive, char negative) {
        char suffix = value.compareTo(BigDecimal.ZERO) < 0 ? negative : positive;
        value = value.abs();
        BigDecimal[] split = value.divideAndRemainder(BigDecimal.ONE);
        BigDecimal degrees = split[0];
        BigDecimal minutes = split[1].multiply(new BigDecimal("60"));
        return String.format("%3.0f\u00B0 %07.4f%c", degrees, minutes, suffix);
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void application() {

        Application.launch(Display.class);
    }
}
