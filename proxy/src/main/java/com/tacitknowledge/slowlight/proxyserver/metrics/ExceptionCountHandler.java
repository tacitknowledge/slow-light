package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link io.netty.channel.ChannelHandler} implementation that counts
 * exception occurred inside channel.
 *
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ExceptionCountHandler extends AbstractChannelHandler
{
    @Monitor(name = "ExceptionCount", type = DataSourceType.GAUGE)
    private AtomicInteger exceptionCount = new AtomicInteger(0);

    public ExceptionCountHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
        Monitors.registerObject(handlerConfig.getName(), this);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
    {
        exceptionCount.getAndIncrement();
    }

    protected int getExceptionCount()
    {
        return exceptionCount.intValue();
    }
}
