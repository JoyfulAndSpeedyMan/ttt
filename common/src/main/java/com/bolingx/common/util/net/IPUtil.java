package com.bolingx.common.util.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPUtil {
    private static final Logger log = LoggerFactory.getLogger(IPUtil.class);

    public static String getIpAddrInner() throws SocketException {
        String ip = "127.0.0.1";
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();

                if (addr instanceof Inet4Address) {
                    ip = addr.getHostAddress();
                    log.debug("{} {}", iface.getDisplayName(), ip);
                    if (!ip.endsWith(".0.1")) {
                        return ip;
                    }
                }
            }
        }

        return ip;
    }
}
