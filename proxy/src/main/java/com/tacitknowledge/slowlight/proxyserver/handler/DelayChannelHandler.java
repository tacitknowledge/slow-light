package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.TimeUnit;

/**
 * DelayChannelHandler class allows to control the size and delay between messages sent over the channel.<br />
 * <br />
 * Handler could be configured from two perspectives:<br />
 * 1. <b>maxDataSize</b> - defines the maximum size of data fragment that must be written by this handler<br />
 * 2. <b>delay</b> - defines the delay which must be applied between data fragments<br />
 * <br />
 * Note: both handler parameters described above are independent and could be set to 0
 * what will cause that particular parameter to be disabled.
 *
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
}
