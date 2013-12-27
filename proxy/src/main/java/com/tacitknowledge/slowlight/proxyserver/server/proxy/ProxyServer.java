package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.AbstractServer;
import io.netty.channel.ChannelInitializer;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class ProxyServer extends AbstractServer
{
    public ProxyServer(final ServerConfig serverConfig)
    {
        super(serverConfig);
    }

    @Override
    protected ChannelInitializer createChannelInitializer()
    {
        return new ProxyChannelInitializer(serverConfig, createEventLoopGroup(PARAM_WORKER_THREADS));
    }
}
