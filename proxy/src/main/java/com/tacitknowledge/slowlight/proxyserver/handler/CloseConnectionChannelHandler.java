package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class CloseConnectionChannelHandler extends AbstractChannelHandler
{
    private static final String PARAM_CLOSE_CONNECTION_AFTER = "closeConnectionAfter";

    public CloseConnectionChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        final long closeConnectionAfter = handlerParams.getLong(PARAM_CLOSE_CONNECTION_AFTER);

        if (closeConnectionAfter == 0)
        {
            closeConnection(ctx);
        }
        else
        {
            scheduleCloseConnection(ctx, closeConnectionAfter);
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        ctx.fireChannelRead(msg);
    }

    @Override
    protected void populateHandlerParams()
    {
        handlerParams.setProperty(PARAM_CLOSE_CONNECTION_AFTER, handlerConfig.getParam(PARAM_CLOSE_CONNECTION_AFTER));
    }

    private void scheduleCloseConnection(final ChannelHandlerContext ctx, final long closeConnectionAfter)
    {
        ctx.executor().schedule(new Runnable()
        {
            @Override
            public void run()
            {
                closeConnection(ctx);
            }
        }, closeConnectionAfter, TimeUnit.MILLISECONDS);
    }

    private void closeConnection(final ChannelHandlerContext ctx)
    {
        ctx.channel().close();
    }
}
