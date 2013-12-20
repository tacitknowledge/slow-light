package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
class ProxyTargetChannelHandler extends AbstractChannelHandler
{
    private Channel sourceChannel;

    public ProxyTargetChannelHandler(final HandlerConfig handlerConfig, final Channel sourceChannel)
    {
        super(handlerConfig);

        this.sourceChannel = sourceChannel;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        if (sourceChannel.isActive())
        {
            final ChannelFutureListener channelFutureListener = getChannelFutureListener(ctx);
            sourceChannel.writeAndFlush(msg).addListener(channelFutureListener);
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception
    {
        closeOnFlush(sourceChannel);
    }

    protected Channel getSourceChannel()
    {
        return sourceChannel;
    }

    protected ChannelFutureListener getChannelFutureListener(final ChannelHandlerContext ctx)
    {
        return new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
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
        };
    }
}
