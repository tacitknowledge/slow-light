package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class InThroughputHandler extends ThroughputHandler
{
    public InThroughputHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final  Object msg) throws Exception
    {
        updateThroughputMetric(msg);
        ctx.fireChannelRead(msg);
    }
}
