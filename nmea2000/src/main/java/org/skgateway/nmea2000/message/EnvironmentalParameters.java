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
package org.skgateway.nmea2000.message;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;

import org.skgateway.nmea2000.Measurements;
import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class EnvironmentalParameters extends Message {
    private static final BigDecimal HUMIDITY_SCALE = new BigDecimal("0.00004");
    private final int sid;
    private final TemperatureSource temperatureSource;
    private final HumiditySource humiditySource;
    private final Quantity<Temperature> temperature;
    private final Quantity<Dimensionless> humidity;
    private final Quantity<Pressure> pressure;
    public EnvironmentalParameters(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = Byte.toUnsignedInt(data.get());
        byte sources = data.get();
        switch (sources & 0x3f) {
        case 0:
            temperatureSource = TemperatureSource.SEA;
            break;
        case 1:
            temperatureSource = TemperatureSource.OUTSIDE;
            break;
        case 2:
            temperatureSource = TemperatureSource.INSIDE;
            break;
        case 3:
            temperatureSource = TemperatureSource.ENGINE_ROOM;
            break;
        case 4:
            temperatureSource = TemperatureSource.MAIN_CABIN;
            break;
        case 5:
            temperatureSource = TemperatureSource.LIVE_WELL;
            break;
        case 6:
            temperatureSource = TemperatureSource.BAIT_WELL;
            break;
        case 7:
            temperatureSource = TemperatureSource.REFRIGERATION;
            break;
        case 8:
            temperatureSource = TemperatureSource.HEATING;
            break;
        case 9:
            temperatureSource = TemperatureSource.DEW_POINT;
            break;
        case 10:
            temperatureSource = TemperatureSource.APPARENT_WIND_CHILL;
            break;
        case 11:
            temperatureSource = TemperatureSource.THEORETICAL_WIND_CHILL;
            break;
        case 12:
            temperatureSource = TemperatureSource.HEAT_INDEX;
            break;
        case 13:
            temperatureSource = TemperatureSource.FREEZER;
            break;
        case 14:
            temperatureSource = TemperatureSource.EXHAUST;
            break;
        default:
            temperatureSource = null;
            break;
        }
        switch ((sources & 0xc0) >>> 6) {
        case 0:
            humiditySource = HumiditySource.INSIDE;
            break;
        case 1:
            humiditySource = HumiditySource.OUTSIDE;
            break;
        default:
            humiditySource = null;
        }
        short val = data.getShort();
        temperature = val == -1 ? null : Measurements.temperature(new BigDecimal(Short.toUnsignedInt(val)).movePointLeft(2));
        val = data.getShort();
        humidity = val == 0x7fff ? null : Measurements.one(new BigDecimal(val).multiply(HUMIDITY_SCALE));
        val = data.getShort();
        pressure = val == -1 ? null : Measurements.pressure(new BigDecimal(Short.toUnsignedInt(val)).movePointRight(2));
    }
    public EnvironmentalParameters(int source, int destination, int priority, int sid, TemperatureSource temperatureSource, HumiditySource humiditySource, Quantity<Temperature> temperature, Quantity<Dimensionless> humidity, Quantity<Pressure> pressure) {
        super(source, destination, priority);
        this.sid = sid;
        this.temperatureSource = temperatureSource;
        this.humiditySource = humiditySource;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    @Override
    public int pgn() {
        return PGN.ENVIRONMENTAL_PARAMETERS;
    }

    public int sid() {
        return sid;
    }

    public TemperatureSource temperatureSource() {
        return temperatureSource;
    }

    public HumiditySource humiditySource() {
        return humiditySource;
    }

    public Quantity<Temperature> temperature() {
        return temperature;
    }

    public Quantity<Dimensionless> humidity() {
        return humidity;
    }

    public Quantity<Pressure> pressure() {
        return pressure;
    }

    @Override
    public String toString() {
        return "EnvironmentalParameters("
                + temperature + " " + temperatureSource
                + ", " + humidity + " " + humiditySource + ", "
                + pressure + ")";
    }

    enum TemperatureSource {
        SEA, OUTSIDE, INSIDE, ENGINE_ROOM, MAIN_CABIN, LIVE_WELL, BAIT_WELL, REFRIGERATION, HEATING,
        DEW_POINT, APPARENT_WIND_CHILL, THEORETICAL_WIND_CHILL, HEAT_INDEX, FREEZER, EXHAUST
    }

    enum HumiditySource {
        INSIDE, OUTSIDE
    }
}

