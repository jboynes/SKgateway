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
package org.skgateway.nmea2000;

/**
 *
 */
public final class PGN {

    public static final int ISO_ADDRESS_CLAIM = 60928;
    public static final int SYSTEM_TIME = 126992;
    public static final int RUDDER = 127245;
    public static final int VESSEL_HEADING = 127250;
    public static final int SPEED = 128259;
    public static final int WATER_DEPTH = 128267;
    public static final int DISTANCE_LOG = 128275;
    public static final int POSITION_RAPID_UPDATE = 129025;
    public static final int COURSE_RAPID_UPDATE = 129026;
    public static final int TIME_AND_DATE = 129033;
    public static final int WIND_DATA = 130306;
    public static final int ENVIRONMENTAL_PARAMETERS = 130311;
    private PGN() {
    }
}
