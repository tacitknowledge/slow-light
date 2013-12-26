package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.RandomStringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class RandomDataChannelHandler extends AbstractChannelHandler
{
    protected static final String PARAM_DATA_FRAGMENTS = "dataFragments";
    protected static final String PARAM_DATA_FRAGMENT_SIZE = "dataFragmentSize";

    public RandomDataChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        final int dataFragments = handlerParams.getInt(PARAM_DATA_FRAGMENTS);
        final int dataFragmentSize = handlerParams.getInt(PARAM_DATA_FRAGMENT_SIZE);

        ctx.executor().schedule(new GenerateDataTask(ctx, dataFragments, dataFragmentSize), 0L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void populateHandlerParams()
    {
        handlerParams.setProperty(PARAM_DATA_FRAGMENTS, handlerConfig.getParam(PARAM_DATA_FRAGMENTS));
        handlerParams.setProperty(PARAM_DATA_FRAGMENT_SIZE, handlerConfig.getParam(PARAM_DATA_FRAGMENT_SIZE));
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
            final ByteBuf channelBuffer = Unpooled.wrappedBuffer(generateData());
            ctx.channel().writeAndFlush(channelBuffer).addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception
                {
                    if (future.isSuccess())
                    {
                        if (dataFragmentIndex++ < dataFragments)
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

        private byte[] generateData()
        {
            return RandomStringUtils.randomAlphanumeric(dataFragmentSize).getBytes();
        }
    }
}
