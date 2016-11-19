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
import java.time.Instant;

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.MessageUtil;

/**
 *
 */
public class SystemTime extends Message {
    private final int sid;
    private final TimeSource timeSource;
    private final Instant instant;

    public enum TimeSource {
        GPS, GLONASS, RADIO, LOCAL_CESIUM, LOCAL_RUBIDIUM, LOCAL_CRYSTAL
    }

    public SystemTime(int source, int destination, int priority, ByteBuffer data) {
        super(source, destination, priority);
        sid = data.get() & 0xff;
        switch (data.get() & 0xf) {
        case 0:
            timeSource = TimeSource.GPS;
            break;
        case 1:
            timeSource = TimeSource.GLONASS;
            break;
        case 2:
            timeSource = TimeSource.RADIO;
            break;
        case 3:
            timeSource = TimeSource.LOCAL_CESIUM;
            break;
        case 4:
            timeSource = TimeSource.LOCAL_RUBIDIUM;
            break;
        case 5:
            timeSource = TimeSource.LOCAL_CRYSTAL;
            break;
        default:
            timeSource = null;
            break;
        }
        instant = MessageUtil.dateTime(data);
    }

    public SystemTime(int source, int destination, int priority, int sid, TimeSource timeSource, Instant instant) {
        super(source, destination, priority);
        this.sid = sid;
        this.timeSource = timeSource;
        this.instant = instant;
    }

    @Override
    public int pgn() {
        return 126992;
    }

    public int sid() {
        return sid;
    }

    public TimeSource timeSource() {
        return timeSource;
    }

    public Instant instant() {
        return instant;
    }

    @Override
    public String toString() {
        return "SystemTime(" + instant + ", " + timeSource + ")";
    }
}
