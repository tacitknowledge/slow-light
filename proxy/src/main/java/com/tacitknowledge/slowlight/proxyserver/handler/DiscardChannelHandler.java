package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;

/**
 * Use this DiscardChannelHandler whenever you what request data to be discarded.
 * In order words when this handler is added to the pipeline then it will cause request data
 * to be delivered only up to this handler and no response to be sent back to the client.
 *
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
