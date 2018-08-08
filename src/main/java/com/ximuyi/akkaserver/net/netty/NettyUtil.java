package com.ximuyi.akkaserver.net.netty;

import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class NettyUtil {

    private static String INVALID_IP_ADDRESS = "-.-.-.-";

    public static String getIpByNettyChannel(Channel channel) {
        if (channel == null) {
            return INVALID_IP_ADDRESS;
        }
        // null if the socket is not connected
        if (channel.localAddress() == null) {
            return INVALID_IP_ADDRESS;
        }
        InetSocketAddress addr = (InetSocketAddress) channel.remoteAddress();
        if(addr == null){
            return INVALID_IP_ADDRESS;
        }
        InetAddress address = addr.getAddress();
        return address == null ? INVALID_IP_ADDRESS : address.getHostAddress();
    }
}
