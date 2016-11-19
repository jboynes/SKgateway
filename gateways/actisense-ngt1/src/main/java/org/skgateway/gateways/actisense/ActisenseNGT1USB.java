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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;

import org.skgateway.nmea2000.Message;
import org.skgateway.nmea2000.MessageFactory;
import org.skgateway.nmea2000.MessageUtil;
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

    static class SerialParser extends Thread {
        private static final byte STX = 0x02;
        private static final byte ETX = 0x03;
        private static final byte DLE = 0x10;

        enum State {
            Init, Escape, Data
        }

        private final ReadableByteChannel source;
        private final Consumer<ByteBuffer> sink;
        private final ByteBuffer in;
        private final ByteBuffer out;
        private State state = State.Init;

        public SerialParser(ReadableByteChannel source, Consumer<ByteBuffer> sink) {
            this.source = source;
            this.sink = sink;
            in = ByteBuffer.allocate(512);
            out = ByteBuffer.allocate(512).order(ByteOrder.LITTLE_ENDIAN);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    in.clear();
                    if (source.read(in) == -1) {
                        return;
                    }
                    in.flip();
                    while (in.hasRemaining()) {
                        handleByte(in.get());
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        private void handleByte(byte b) {
            switch (state) {
            case Init:
                if (b == DLE) {
                    state = State.Escape;
                }
                break;
            case Escape:
                switch (b) {
                case STX:
                    state = State.Data;
                    out.clear();
                    break;
                case ETX:
                    out.flip();
                    sink.accept(out);
                    out.clear();
                    state = State.Init;
                    break;
                default:
                    out.put(b);
                    state = State.Data;
                }
                break;
            case Data:
                if (b == DLE) {
                    state = State.Escape;
                } else {
                    out.put(b);
                }
                break;
            }
        }
    }

    static class MessageParser implements Consumer<ByteBuffer> {
        private static final byte N2K_MESSAGE = (byte) 0x93;
        private static final byte NGT_MESSAGE = (byte) 0xa0;

        private final Consumer<Message> sink;
        private final boolean delay;
        private int lastTimestamp;

        MessageParser(Consumer<Message> sink, boolean delay) {
            this.sink = sink;
            this.delay = delay;
        }

        @Override
        public void accept(ByteBuffer buffer) {
            buffer.mark();
            byte checksum = 0;
            while (buffer.hasRemaining()) {
                checksum += buffer.get();
            }
            buffer.rewind();
            if (checksum != 0) {
                return;
            }

            byte type = buffer.get();
            int length = buffer.get() & 0xff;
            switch (type) {
            case NGT_MESSAGE:
                break;
            case N2K_MESSAGE:
                if (buffer.remaining() < 11) {
                    return;
                }
                int priority = buffer.get() & 0xff;
                int pgn = MessageUtil.pgn(buffer);
                int destination = buffer.get() & 0xff;
                int source = buffer.get() & 0xff;
                int timestamp = buffer.getInt();
                int dataLength = buffer.get() & 0xff;
                buffer.limit(buffer.position() + dataLength);
                if (dataLength != buffer.remaining()) {
                    return;
                }
                Message message = MessageFactory.fromData(pgn, source, destination, priority, buffer);

                if (delay && timestamp > lastTimestamp && lastTimestamp > 0) {
                    try {
                        Thread.sleep(timestamp - lastTimestamp);
                    } catch (InterruptedException e) {
                    }
                }
                lastTimestamp = timestamp;
                sink.accept(message);
                break;
            }
        }
    }
}
