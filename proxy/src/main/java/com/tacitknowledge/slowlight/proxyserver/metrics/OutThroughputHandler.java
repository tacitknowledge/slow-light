package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class OutThroughputHandler extends ThroughputHandler
{
    public OutThroughputHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        super.updateThroughputMetric(msg);
        ctx.write(msg, promise);
    }
}
