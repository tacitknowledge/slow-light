package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.DynamicChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

/**
 * Proxy pipeline initializer used for proxy server construction.
 * Initializer adds all required proxy handlers to the channel pipeline.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ProxyChannelInitializer extends DynamicChannelInitializer
{
    public static final String PROXY_HANDLER_NAME = "proxyHandler";
    public static final String PARAM_HOST = "host";
    public static final String PARAM_PORT = "port";
    private EventLoopGroup clientWorkerGroup;

    public ProxyChannelInitializer(final ServerConfig serverConfig, final EventLoopGroup clientWorkerGroup)
    {
        super(serverConfig);

        this.clientWorkerGroup = clientWorkerGroup;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception
    {
        super.initChannel(ch);
        ch.config().setAutoRead(false);

        final String host = serverConfig.getParam(PARAM_HOST);
        final int port = Integer.parseInt(serverConfig.getParam(PARAM_PORT));
        final ProxyChannelHandler handler = new ProxyChannelHandler(HandlerConfig.EMPTY, host, port, clientWorkerGroup);

        ch.pipeline().addLast(PROXY_HANDLER_NAME, handler);
    }

    protected ServerConfig getServerConfig()
    {
        return serverConfig;
    }

    protected EventLoopGroup getClientWorkerGroup()
    {
        return clientWorkerGroup;
    }
}
