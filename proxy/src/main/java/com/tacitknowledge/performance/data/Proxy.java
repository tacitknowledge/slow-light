package com.tacitknowledge.performance.data;

import com.tacitknowledge.performance.Component;

import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Proxy implements Component
{
    private final String remoteHost;
    private final int remotePort;

    public Proxy(final String remoteHost, final int remotePort)
    {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.config().setAutoRead(false);
        ch.config().setMaxMessagesPerRead(1); //is this necessary?
        ch.pipeline().addLast(new ProxyFrontendHandler(remoteHost, remotePort));
    }
}
