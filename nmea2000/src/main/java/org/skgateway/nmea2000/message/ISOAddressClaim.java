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

import java.nio.ByteBuffer;

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.PGN;

/**
 *
 */
public class ISOAddressClaim extends Message {
    private final int uniqueId;
    private final int manufacturerCode;
    private final int deviceInstance;
    private final int deviceFunction;
    private final DeviceClass deviceClass;
    private final int deviceClassInstance;
    private final IndustryGroup industryGroup;
    private final boolean selfConfigurable;
    public ISOAddressClaim(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        int val = data.getInt();
        uniqueId = val & 0x1fffff;
        manufacturerCode = (val >>> 21) & 0x7ff;
        deviceInstance = Byte.toUnsignedInt(data.get());
        deviceFunction = Byte.toUnsignedInt(data.get());
        switch ((data.get() >>> 1) & 0x7f) {
        case 0:
            deviceClass = DeviceClass.RESERVED;
            break;
        case 10:
            deviceClass = DeviceClass.SYSTEM_TOOLS;
            break;
        case 20:
            deviceClass = DeviceClass.SAFETY_SYSTEM;
            break;
        case 25:
            deviceClass = DeviceClass.INTERNETWORK_DEVICE;
            break;
        case 30:
            deviceClass = DeviceClass.ELECTRICAL_DISTRIBUTION;
            break;
        case 35:
            deviceClass = DeviceClass.ELECTRICAL_GENERATION;
            break;
        case 40:
            deviceClass = DeviceClass.STEERING_AND_CONTROL_SURFACES;
            break;
        case 50:
            deviceClass = DeviceClass.PROPULSION;
            break;
        case 60:
            deviceClass = DeviceClass.NAVIGATION;
            break;
        case 70:
            deviceClass = DeviceClass.COMMUNICATION;
            break;
        case 75:
            deviceClass = DeviceClass.SENSOR_COMMUNICATION;
            break;
        case 80:
            deviceClass = DeviceClass.INSTRUMENTATION;
            break;
        case 85:
            deviceClass = DeviceClass.EXTERNAL_ENVIRONMENT;
            break;
        case 90:
            deviceClass = DeviceClass.INTERNAL_ENVIRONMENT;
            break;
        case 100:
            deviceClass = DeviceClass.DECK_CARGO_FISHING;
            break;
        case 120:
            deviceClass = DeviceClass.DISPLAY;
            break;
        case 125:
            deviceClass = DeviceClass.ENTERTAINMENT;
            break;
        default:
            deviceClass = null;
            break;
        }
        val = data.get();
        deviceClassInstance = val & 0xf;
        switch ((val >>> 4) & 0x7) {
        case 0:
            industryGroup = IndustryGroup.GLOBAL;
            break;
        case 1:
            industryGroup = IndustryGroup.HIGHWAY;
            break;
        case 2:
            industryGroup = IndustryGroup.AGRICULTURE;
            break;
        case 3:
            industryGroup = IndustryGroup.CONSTRUCTION;
            break;
        case 4:
            industryGroup = IndustryGroup.MARINE;
            break;
        case 5:
            industryGroup = IndustryGroup.INDUSTRIAL;
            break;
        default:
            industryGroup = null;
            break;
        }
        selfConfigurable = (val & 0x80) != 0;
    }
    public ISOAddressClaim(int source, int destination, int priority, int uniqueId, int manufacturerCode, int deviceInstance, int deviceFunction, DeviceClass deviceClass, int deviceClassInstance, IndustryGroup industryGroup, boolean selfConfigurable) {
        super(source, destination, priority);
        this.uniqueId = uniqueId;
        this.manufacturerCode = manufacturerCode;
        this.deviceInstance = deviceInstance;
        this.deviceFunction = deviceFunction;
        this.deviceClass = deviceClass;
        this.deviceClassInstance = deviceClassInstance;
        this.industryGroup = industryGroup;
        this.selfConfigurable = selfConfigurable;
    }

    @Override
    public int pgn() {
        return PGN.ISO_ADDRESS_CLAIM;
    }

    public int uniqueId() {
        return uniqueId;
    }

    public int manufacturerCode() {
        return manufacturerCode;
    }

    public int deviceInstance() {
        return deviceInstance;
    }

    public int deviceFunction() {
        return deviceFunction;
    }

    public DeviceClass deviceClass() {
        return deviceClass;
    }

    public int deviceClassInstance() {
        return deviceClassInstance;
    }

    public IndustryGroup industryGroup() {
        return industryGroup;
    }

    public boolean selfConfigurable() {
        return selfConfigurable;
    }

    @Override
    public String toString() {
        return "ISOAddressClaim(" + source() + ", " + uniqueId
                + ", " + manufacturerCode
                + ", " + deviceInstance
                + ", " + deviceFunction
                + ", " + deviceClass
                + ", " + deviceClassInstance
                + ", " + industryGroup
                + ", " + selfConfigurable
                + ")";
    }

    private enum DeviceClass {
        RESERVED, SYSTEM_TOOLS, SAFETY_SYSTEM, INTERNETWORK_DEVICE,
        ELECTRICAL_DISTRIBUTION, ELECTRICAL_GENERATION, STEERING_AND_CONTROL_SURFACES,
        PROPULSION, NAVIGATION, COMMUNICATION, SENSOR_COMMUNICATION, INSTRUMENTATION,
        EXTERNAL_ENVIRONMENT, INTERNAL_ENVIRONMENT, DECK_CARGO_FISHING,
        DISPLAY, ENTERTAINMENT
    }

    private enum IndustryGroup {
        GLOBAL, HIGHWAY, AGRICULTURE, CONSTRUCTION, MARINE, INDUSTRIAL
    }
}

