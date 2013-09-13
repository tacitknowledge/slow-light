package com.tacitknowledge.slowlight.proxyserver.degrade;

import java.util.concurrent.TimeUnit;

import com.tacitknowledge.slowlight.proxyserver.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Delay implements Component
{
    private final int delay;

    private final boolean delayOnActive;
    private final boolean delayOnRead;

    public Delay(final int delay, final boolean delayOnActive, final boolean delayOnRead)
    {
        this.delay = delay;
        this.delayOnActive = delayOnActive;
        this.delayOnRead = delayOnRead;
    }

    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.config().setAutoRead(false);
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter()
        {
            @Override
            public void channelActive(final ChannelHandlerContext ctx) throws Exception
            {
                if (delayOnActive)
                {
                    ctx.executor().schedule(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ctx.fireChannelActive();
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
                else
                {
                    super.channelActive(ctx);
                }
            }

            @Override
            public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
            {
                if (delayOnRead)
                {
                    ctx.executor().schedule(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ctx.fireChannelRead(msg);
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
                else
                {
                    super.channelRead(ctx, msg);
                }
            }
        });
    }
}
