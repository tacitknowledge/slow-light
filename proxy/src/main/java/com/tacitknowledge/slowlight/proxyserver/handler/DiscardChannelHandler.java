package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DiscardChannelHandler extends AbstractChannelHandler
{
    public DiscardChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        ctx.read();
    }
}
