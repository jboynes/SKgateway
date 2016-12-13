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
package org.skgateway.services.jmdns;

import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 *
 */
public class ListServices {

    public static void main(String[] args) throws Exception {
        NetworkInterface en1 = NetworkInterface.getByName("en0");
        InetAddress address = en1.getInetAddresses().nextElement();
        JmDNS jmDNS = JmDNS.create(address);
        for (ServiceInfo serviceInfo : jmDNS.list("_http._tcp.local.")) {
            System.out.println("serviceInfo = " + serviceInfo);
        }
    }
}
