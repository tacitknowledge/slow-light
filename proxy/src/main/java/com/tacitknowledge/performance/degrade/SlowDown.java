package com.tacitknowledge.performance.degrade;

import java.util.concurrent.TimeUnit;

import com.tacitknowledge.performance.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;

/**
 * TODO: refactor this. For higher traffic rates use buffers larger then 1 byte size and calculate appropriate delays
 * TODO: separate configs for up and down traffics
 *
 * @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SlowDown implements Component
{
    private final long millisecondsPerByte;

    public SlowDown(final long millisecondsPerByte)
    {
        this.millisecondsPerByte = millisecondsPerByte;
    }

    @Override
    public void initChannel(final SocketChannel ch)
    {
        ch.config().setReceiveBufferSize(1);
        ch.pipeline().addLast(new SlowDownHandler(millisecondsPerByte));
    }

    public static class SlowDownHandler extends ChannelDuplexHandler
    {
        private final long millisecondsPerByte;

        public SlowDownHandler(final long millisecondsPerByte)
        {
            this.millisecondsPerByte = millisecondsPerByte;
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
        {
            ctx.executor().schedule(new Runnable()
            {
                @Override
                public void run()
                {
                    ctx.fireChannelRead(msg);
                }
            }, millisecondsPerByte, TimeUnit.MILLISECONDS);
        }

        @Override
        public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
        {
            final ByteBuf buf = (ByteBuf) msg;
            Runnable task = new Runnable()
            {
                private int ix = buf.readerIndex();
                @Override
                public void run()
                {
                    if(ix < buf.writerIndex()) {
                        ctx.writeAndFlush(Unpooled.wrappedBuffer(buf.copy(ix, 1)));
                        ix++;
                        ctx.executor().schedule(this, millisecondsPerByte, TimeUnit.MILLISECONDS);
                    } else {
                        promise.setSuccess();
                    }
                }
            };

            ctx.executor().submit(task);
        }
    }
}
