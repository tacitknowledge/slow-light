package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.AbstractServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class ProxyServer extends AbstractServer
{
    public ProxyServer(final ServerConfig serverConfig)
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

    protected EventLoopGroup createClientWorkerGroup()
    {
        return new NioEventLoopGroup();
    }

    @Override
    protected ChannelInitializer createChannelInitializer()
    {
        return new ProxyChannelInitializer(serverConfig, createClientWorkerGroup());
    }
}
