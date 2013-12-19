package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ExceptionCountHandler extends AbstractChannelHandler
{
    @Monitor(name = "ExceptionCount", type = DataSourceType.GAUGE)
    private int exceptionCount;

    public ExceptionCountHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
        Monitors.registerObject(handlerConfig.getName(), this);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
    {
        exceptionCount++;
    }

    protected int getExceptionCount()
    {
        return exceptionCount;
    }
}
