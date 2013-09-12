package com.tacitknowledge.performance.data;

import com.tacitknowledge.performance.Component;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SimpleMessage implements Component
{
    private final String message;

    public SimpleMessage(final String message)
    {
        this.message = message;
    }

    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.pipeline().addLast(new StringEncoder());
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(final ChannelHandlerContext ctx) throws Exception
            {
                ctx.channel().writeAndFlush(message).addListener(ChannelFutureListener.CLOSE);
            }
        });
    }
}
