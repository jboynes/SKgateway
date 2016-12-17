package org.skgateway.server;

import org.skgateway.gateways.actisense.ActisenseNGT1USB;
import org.skgateway.server.nmea2000.PgnMapper;

import javax.json.JsonObject;

public class DumpServer {
    public static void main(String[] args) throws Exception {
        PgnMapper pgnMapper = new PgnMapper("vessels.self");
        new ActisenseNGT1USB("tty.usbserial-2C542", message -> {
            JsonObject json = pgnMapper.map(message);
            if (json != null) {
                System.out.println(json);
            }
        });
    }
}
