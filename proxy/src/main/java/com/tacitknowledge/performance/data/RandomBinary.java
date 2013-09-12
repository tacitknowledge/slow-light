package com.tacitknowledge.performance.data;

import org.apache.commons.lang.math.RandomUtils;

import com.tacitknowledge.performance.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class RandomBinary implements Component
{
    private final static int BUF_SIZE = 2048;

    private final int maxSize;

    public RandomBinary(final int maxSize)
    {
        this.maxSize = maxSize;
    }

    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(final ChannelHandlerContext ctx) throws Exception
            {
                ctx.executor().submit(new Task(ctx));
            }

            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
            {
                ctx.channel().close();
            }
        });
    }

    private final class Task implements Runnable{
        private final ChannelHandlerContext ctx;
        private int totalSent = 0;

        private Task(final ChannelHandlerContext ctx)
        {
            this.ctx = ctx;
        }

        @Override
        public void run()
        {
            int toSend = Math.min(BUF_SIZE, maxSize - totalSent);
            ByteBuf buf = ctx.channel().alloc().buffer(toSend);
            totalSent += toSend;

            while(buf.isWritable()) {
                buf.writeByte(RandomUtils.nextInt(256));
            }

            ctx.channel().writeAndFlush(buf).addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception
                {
                    if(future.isSuccess() && totalSent < maxSize) {
                        ctx.executor().submit(Task.this);
                    } else {
                        ctx.channel().close();
                    }
                }
            });

        }
    }
}
