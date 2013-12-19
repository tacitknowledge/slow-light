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

    private long closeConnectionAfter;

    public CloseConnectionChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);

        closeConnectionAfter = Long.parseLong(handlerConfig.getParam(PARAM_CLOSE_CONNECTION_AFTER));
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.executor().schedule(new Runnable()
        {
            @Override
            public void run()
            {
                ctx.channel().close();
            }
        }, closeConnectionAfter, TimeUnit.MILLISECONDS);

        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        ctx.fireChannelRead(msg);
    }
}
