package com.tacitknowledge.slowlight.proxyserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.TimeUnit;

/**
 * This task is used to split given channel message into data fragments and will send them a separate messages to the next handlers.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class WriteDataFragmentsTask implements Runnable
{
    private ChannelHandlerContext ctx;
    private ByteBuf msg;
    private ChannelPromise promise;

    private int maxDataSize;
    private long delay;

    private WriteDataFragmentsTask thisTask;

    public WriteDataFragmentsTask(final ChannelHandlerContext ctx,
                                   final ByteBuf msg,
                                   final ChannelPromise promise,
                                   final int maxDataSize,
                                   final long delay)
    {
        this.ctx = ctx;
        this.msg = msg;
        this.promise = promise;

        this.maxDataSize = maxDataSize;
        this.delay = delay;

        this.thisTask = this;
    }

    @Override
    public void run()
    {
        if (msg.isReadable())
        {
            ctx.writeAndFlush(readDataFragment(msg)).addListener(new WriteDataListener());
        }
    }

    private ByteBuf readDataFragment(final ByteBuf readChannelBuffer)
    {
        final int readableBytes = readChannelBuffer.readableBytes();
        final int bytesToRead = maxDataSize == 0 || readableBytes < maxDataSize ? readableBytes : maxDataSize;

        final ByteBuf writeChannelBuffer = Unpooled.buffer(bytesToRead);
        readChannelBuffer.readBytes(writeChannelBuffer, bytesToRead);

        return writeChannelBuffer;
    }

    /**
     * Listener class to schedule next write data fragment task.
     */
    protected class WriteDataListener implements ChannelFutureListener
    {
        @Override
        public void operationComplete(final ChannelFuture future) throws Exception
        {
            if (future.isSuccess())
            {
                if (msg.readableBytes() > 0)
                {
                    ctx.executor().schedule(thisTask, delay, TimeUnit.MILLISECONDS);
                }
                else
                {
                    promise.setSuccess();
                }
            }
            else
            {
                future.channel().close();
            }
        }
    }
}
