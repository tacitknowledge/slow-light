package com.tacitknowledge.slowlight.proxyserver.degrade;

import com.tacitknowledge.slowlight.proxyserver.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class PeerClose implements Component
{
    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(final ChannelHandlerContext ctx) throws Exception
            {
                ctx.channel().close();
            }
        });
    }
}
