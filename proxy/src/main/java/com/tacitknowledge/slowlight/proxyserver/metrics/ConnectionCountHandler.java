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
public class ConnectionCountHandler extends AbstractChannelHandler
{
    private int openConnectionCount;

    public ConnectionCountHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
        Monitors.registerObject(handlerConfig.getName(), this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        openConnectionCount++;
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        openConnectionCount--;
        super.channelInactive(ctx);
    }

    @Monitor(name = "OpenConnectionCount", type = DataSourceType.GAUGE)
    public int getOpenConnectionCount()
    {
        return openConnectionCount;
    }
}
