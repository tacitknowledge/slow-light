package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * Use this EchoChannelHandler whenever you what request data to be echoed back to the client.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class EchoChannelHandler extends AbstractChannelHandler
{
    public EchoChannelHandler(final HandlerConfig handlerConfig)
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
        ctx.channel().writeAndFlush(msg).addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    ctx.channel().read();
                }
                else
                {
                    future.channel().close();
                }
            }
        });
    }
}
