package com.tacitknowledge.slowlight.proxyserver.systest.util.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
* @author Alexandr Donciu (adonciu@tacitknowledge.com)
*/
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelDuplexHandler
{
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
