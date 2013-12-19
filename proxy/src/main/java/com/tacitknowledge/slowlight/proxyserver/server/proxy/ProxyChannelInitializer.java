package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.DynamicChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class ProxyChannelInitializer extends DynamicChannelInitializer
{
    private static final String PROXY_HANDLER_NAME = "proxyHandler";
    private static final String PARAM_HOST = "host";
    private static final String PARAM_PORT = "port";

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

        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(PROXY_HANDLER_NAME,
                new ProxyChannelHandler(HandlerConfig.EMPTY, serverConfig.getParam(PARAM_HOST),
                Integer.parseInt(serverConfig.getParam(PARAM_PORT)), clientWorkerGroup));
    }
}
