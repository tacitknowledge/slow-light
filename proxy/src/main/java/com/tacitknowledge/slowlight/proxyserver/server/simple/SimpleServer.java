package com.tacitknowledge.slowlight.proxyserver.server.simple;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.AbstractServer;
import com.tacitknowledge.slowlight.proxyserver.server.DynamicChannelInitializer;
import io.netty.channel.ChannelInitializer;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class SimpleServer extends AbstractServer
{
    public SimpleServer(final ServerConfig serverConfig)
    {
        super(serverConfig);
    }

    @Override
    protected ChannelInitializer createChannelInitializer()
    {
        return new DynamicChannelInitializer(serverConfig);
    }
}
