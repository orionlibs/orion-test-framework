package com.yapily.orione2e.utils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

public class NetUtils
{
    // Attempts to open a TCP connection to host:port with a short timeout.
    public static boolean canConnectTcp(String host, int port, Duration timeout)
    {
        try(Socket socket = new Socket())
        {
            socket.connect(new InetSocketAddress(host, port), (int)timeout.toMillis());
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}
