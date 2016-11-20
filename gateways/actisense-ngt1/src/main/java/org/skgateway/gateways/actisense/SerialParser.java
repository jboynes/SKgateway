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
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;

/**
 *
 */
public class SerialParser extends Thread {
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
