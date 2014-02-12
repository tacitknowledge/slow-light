package com.tacitknowledge.slowlight.proxyserver.server.simple;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.AbstractServer;
import com.tacitknowledge.slowlight.proxyserver.server.DynamicChannelInitializer;
import io.netty.channel.ChannelInitializer;

/**
 * This class represents a simple (no initial logic by default) server implementation based on netty.
 * Simple server implementation allows someone to construct a server
 * by simply passing in server configurations (things like params, pipeline handlers, etc).
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
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
