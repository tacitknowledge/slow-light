package com.tacitknowledge.slowlight.proxyserver.server.simple;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.AbstractServer;
import com.tacitknowledge.slowlight.proxyserver.server.DynamicChannelInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class SimpleServer extends AbstractServer
{
    public SimpleServer(final ServerConfig serverConfig)
    {
        super(serverConfig);
    }

    @Override
    protected EventLoopGroup createBossGroup()
    {
        return new NioEventLoopGroup();
    }

    @Override
    protected EventLoopGroup createWorkerGroup()
    {
        return new NioEventLoopGroup();
    }

    @Override
    protected ChannelInitializer createChannelInitializer()
    {
        return new DynamicChannelInitializer(serverConfig);
    }
}
