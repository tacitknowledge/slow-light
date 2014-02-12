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
 * RandomDataChannelHandler class allows to generate random data as a response back to the client.
 * For example this handler could be used if you want to simulate that server respond with some junk data of an arbitrary size.<br />
 * <br />
 * Handler parameters:<br />
 * 1. <b>dataFragmentSize</b> - defines the size of data fragment that must be generated<br />
 * 2. <b>dataFragments</b> - defines the number data fragments to be generated<br />
 *
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

        ctx.executor().schedule(new GenerateRandomDataTask(ctx, dataFragments, dataFragmentSize), 0L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void populateHandlerParams()
    {
        handlerParams.setProperty(PARAM_DATA_FRAGMENTS, handlerConfig.getParam(PARAM_DATA_FRAGMENTS));
        handlerParams.setProperty(PARAM_DATA_FRAGMENT_SIZE, handlerConfig.getParam(PARAM_DATA_FRAGMENT_SIZE));
    }

    protected class GenerateRandomDataTask implements Runnable
    {
        private final int dataFragments;
        private final int dataFragmentSize;
        private int dataFragmentIndex;

        private ChannelHandlerContext ctx;

        private final GenerateRandomDataTask generateRandomDataTask;

        protected GenerateRandomDataTask(final ChannelHandlerContext ctx, final int dataFragments, final int dataFragmentSize)
        {
            this.dataFragments = dataFragments;
            this.dataFragmentSize = dataFragmentSize;

            this.ctx = ctx;
            this.generateRandomDataTask = this;
        }

        @Override
        public void run()
        {
            final ByteBuf channelBuffer = Unpooled.wrappedBuffer(generateData());
            ctx.channel().writeAndFlush(channelBuffer).addListener(new GenerateDataListener());
        }

        private byte[] generateData()
        {
            return RandomStringUtils.randomAlphanumeric(dataFragmentSize).getBytes();
        }

        /**
         * Listener class to schedule next generate random data fragment task.
         */
        protected class GenerateDataListener implements ChannelFutureListener
        {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    if (dataFragmentIndex++ < dataFragments)
                    {
                        ctx.executor().schedule(generateRandomDataTask, 0, TimeUnit.MILLISECONDS);
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
        }
    }
}
