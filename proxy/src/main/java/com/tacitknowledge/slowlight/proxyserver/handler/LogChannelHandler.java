package com.tacitknowledge.slowlight.proxyserver.handler;

import com.netflix.servo.monitor.Monitors;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class LogChannelHandler extends AbstractChannelHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(LogChannelHandler.class);

    public LogChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);

        Monitors.registerObject("Log Channel Handler", this);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, Object msg, final ChannelPromise promise) throws Exception
    {
        LOG.info("write data: " + msg);

        ctx.write(msg, promise);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        LOG.info("read data: " + msg);

        ctx.fireChannelRead(msg);
    }
}
