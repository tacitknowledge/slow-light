package com.tacitknowledge.slowlight.proxyserver.systest.util.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
* @author Alexandr Donciu (adonciu@tacitknowledge.com)
*/
class ClientChannelHandler extends ChannelDuplexHandler
{
    private Lock lock;
    private Condition responseReady;

    private int responseSize;
    private ServerResponse response;

    public ClientChannelHandler(final Lock lock, final Condition responseReady, final ServerResponse response, final int responseSize)
    {
        this.lock = lock;
        this.responseReady = responseReady;

        this.responseSize = responseSize;
        this.response = response;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        final ByteBuf msgByteBuf = (ByteBuf) msg;

        final byte[] msgBytes = new byte[msgByteBuf.readableBytes()];
        msgByteBuf.readBytes(msgBytes);
        setResponse(msgBytes);

        ctx.read();
    }

    private void setResponse(final byte[] bytes)
    {
        lock.lock();
        try
        {
            response.set(bytes);
            if (response.size() == responseSize)
            {
                responseReady.signal();
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        ctx.write(msg, promise);
    }
}
