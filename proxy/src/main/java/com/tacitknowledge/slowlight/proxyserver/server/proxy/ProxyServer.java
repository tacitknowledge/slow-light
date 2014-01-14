package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.AbstractServer;
import io.netty.channel.ChannelInitializer;

/**
 * This class represents a proxy server implementation, as the name suggests, a given server
 * constructed using this class will have an initial configuration which will enable this server to act as a proxy server.
 *
 * Server parameters:
 * 1. host - the target (remote) host to be proxied
 * 2. port - the target (remote) port to be proxied
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
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
