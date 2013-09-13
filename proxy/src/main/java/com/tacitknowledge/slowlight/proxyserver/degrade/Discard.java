package com.tacitknowledge.slowlight.proxyserver.degrade;

import java.util.concurrent.TimeUnit;

import com.tacitknowledge.slowlight.proxyserver.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Discard implements Component
{
    private final long timeout;

    public Discard(final long timeout)
    {
        this.timeout = timeout;
    }

    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(final ChannelHandlerContext ctx) throws Exception
            {
                ctx.executor().schedule(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ctx.channel().close();
                    }
                }, timeout, TimeUnit.MILLISECONDS);
            }

            @Override
            public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
            {
                //discard
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
            {
                ctx.channel().close();
            }
        });
    }
}
