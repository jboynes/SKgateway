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
package org.skgateway.gateways.actisense;

import java.io.IOException;
import java.nio.channels.Channels;
import java.util.function.Consumer;

import org.skgateway.nmea2000.Message;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

/**
 * Wrapper for an Actisense NGT-1-USB gateway.
 */
public class ActisenseNGT1USB {

    private final SerialPort port;

    public ActisenseNGT1USB(String portName, Consumer<Message> listener) throws IOException {
        try {
            CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(portName);
            port = (SerialPort) id.open("", 0);
            port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
            throw new IOException("Unable to open serial port: " + portName, e);
        }
        SerialParser parser = new SerialParser(Channels.newChannel(port.getInputStream()), new MessageParser(listener, false));
        parser.start();
    }

    public void send(Message message) {
    }

}
