package it.polimi.ingsw.utils;

public class Constant {

    private static int port = 9000;
    private static String hostIp = "127.0.0.1";

    public static int port() {
        return  port;
    }

    public static String hostIp() {
        return hostIp;
    }

    public static void setPort(int port) {
        Constant.port = port;
    }

    public static void setHostIp(String hostIp) {
        Constant.hostIp = hostIp;
    }
}
