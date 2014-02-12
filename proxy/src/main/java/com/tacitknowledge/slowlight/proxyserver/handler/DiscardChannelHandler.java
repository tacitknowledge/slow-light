package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * Use this DiscardChannelHandler whenever you what request data to be discarded.
 * In order words when this handler is added to the pipeline then it will cause request data
 * to be delivered only up to this handler and no response to be sent back to the client.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DiscardChannelHandler extends AbstractChannelHandler
{
    private static final String PARAM_ENABLED = "enabled";

    public DiscardChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    protected void populateHandlerParams()
    {
        handlerParams.setProperty(PARAM_ENABLED, handlerConfig.getParam(PARAM_ENABLED, false));
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        final boolean discard = handlerParams.getBoolean(PARAM_ENABLED, true);
        if (discard)
        {
            ReferenceCountUtil.release(msg);
            ctx.read();
        }
        else
        {
            ctx.fireChannelRead(msg);
        }
    }
}
