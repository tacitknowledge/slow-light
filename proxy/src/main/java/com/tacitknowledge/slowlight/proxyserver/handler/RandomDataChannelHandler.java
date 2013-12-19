package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class RandomDataChannelHandler extends AbstractChannelHandler
{
    protected static final String PARAM_DATA_FRAGMENTS = "dataFragments";
    protected static final String PARAM_DATA_FRAGMENT_SIZE = "dataFragmentSize";

    private int dataFragments;
    private int dataFragmentSize;

    public RandomDataChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);

        dataFragments = Integer.parseInt(handlerConfig.getParam(PARAM_DATA_FRAGMENTS));
        dataFragmentSize = Integer.parseInt(handlerConfig.getParam(PARAM_DATA_FRAGMENT_SIZE));
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        ctx.executor().schedule(new GenerateDataTask(ctx, dataFragments, dataFragmentSize), 0L, TimeUnit.MILLISECONDS);
    }

    private byte[] generateData(final int dataSize, final int i)
    {
        byte[] data = new byte[dataSize];

        byte dataByte = (byte) ('a' + (Math.random() * 26));

        for (int j = 0; j < dataSize; j++)
        {
            data[j] = dataByte;
        }

        return data; //RandomStringUtils.randomAlphanumeric(dataFragmentSize).getBytes();
    }

    private class GenerateDataTask implements Runnable
    {
        private final int dataFragments;
        private final int dataFragmentSize;
        private int dataFragmentIndex;

        private ChannelHandlerContext ctx;

        private final GenerateDataTask generateDataTask;

        private GenerateDataTask(final ChannelHandlerContext ctx, final int dataFragments, final int dataFragmentSize)
        {
            this.dataFragments = dataFragments;
            this.dataFragmentSize = dataFragmentSize;

            this.ctx = ctx;
            this.generateDataTask = this;
        }

        @Override
        public void run()
        {
            final ByteBuf channelBuffer = Unpooled.wrappedBuffer(generateData(dataFragmentSize, dataFragmentIndex++));
            ctx.channel().writeAndFlush(channelBuffer).addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception
                {
                    if (future.isSuccess())
                    {
                        if (dataFragmentIndex < dataFragments)
                        {
                            ctx.executor().schedule(generateDataTask, 0, TimeUnit.MILLISECONDS);
                        }
                        else
                        {
                            ctx.channel().read();
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
}
