package com.tacitknowledge.slowlight.proxyserver;

import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Component
{
    void initChannel(SocketChannel ch);
}
