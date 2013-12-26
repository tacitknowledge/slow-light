package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.TimeUnit;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DelayChannelHandler extends AbstractChannelHandler
{
    protected static final String PARAM_MAX_DATA_SIZE = "maxDataSize";
    protected static final String PARAM_DELAY = "delay";

    public DelayChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        final int maxDataSize = handlerParams.getInt(PARAM_MAX_DATA_SIZE);
        final long delay = handlerParams.getLong(PARAM_DELAY);

        ctx.executor().schedule(new WriteDataFragmentsTask(ctx, (ByteBuf) msg, promise, maxDataSize, delay), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void populateHandlerParams()
    {
        handlerParams.setProperty(PARAM_MAX_DATA_SIZE, handlerConfig.getParam(PARAM_MAX_DATA_SIZE));
        handlerParams.setProperty(PARAM_DELAY, handlerConfig.getParam(PARAM_DELAY));
    }

    private class WriteDataFragmentsTask implements Runnable
    {
        private ChannelHandlerContext ctx;
        private ByteBuf msg;
        private ChannelPromise promise;

        private int maxDataSize;
        private long delay;

        private WriteDataFragmentsTask writeDataFragmentsTask;

        private WriteDataFragmentsTask(final ChannelHandlerContext ctx,
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

            this.writeDataFragmentsTask = this;
        }

        @Override
        public void run()
        {
            if (msg.isReadable())
            {
                ctx.writeAndFlush(readDataFragment(msg)).addListener(new ChannelFutureListener()
                {
                    @Override
                    public void operationComplete(final ChannelFuture future) throws Exception
                    {
                        if (future.isSuccess())
                        {
                            if (msg.readableBytes() > 0)
                            {
                                ctx.executor().schedule(writeDataFragmentsTask, delay, TimeUnit.MILLISECONDS);
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
                });
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
    }
}
