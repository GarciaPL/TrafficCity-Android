package pl.garciapl.trafficcity.rest;

import java.net.InetAddress;

/**
 * Created by lukasz on 14.12.14.
 */
public class Reachable {

    private static int TIMEOUT = 5000;

    public static boolean checkConnection(String ipAddress) {
        try {
            if (ipAddress != null) {
                InetAddress in = InetAddress.getByName(ipAddress);
                return in.isReachable(TIMEOUT);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
