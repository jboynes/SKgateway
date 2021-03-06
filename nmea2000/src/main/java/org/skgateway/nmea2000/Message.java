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
public abstract class Message {

    private final int source;
    private final int destination;
    private final int priority;

    protected Message(int source, int destination, int priority) {
        this.source = source;
        this.destination = destination;
        this.priority = priority;
    }

    public abstract int pgn();

    public int source() {
        return source;
    }

    public int destination() {
        return destination;
    }

    public int priority() {
        return priority;
    }
}
