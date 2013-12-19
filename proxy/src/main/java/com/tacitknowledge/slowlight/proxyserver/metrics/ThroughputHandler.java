package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ThroughputHandler extends AbstractChannelHandler
{
    private long frameReadBytes;
    private long channelReadBytes;

    public static final TimeUnit SECONDS = TimeUnit.SECONDS;
    public static final TimeUnit MILLISECONDS = TimeUnit.MILLISECONDS;

    private Date startDate;

    public ThroughputHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);
        startDate = new Date();

        Monitors.registerObject(handlerConfig.getName(), this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        final long readableBytes = ((ByteBuf) msg).readableBytes();

        if(readableBytes > Long.MAX_VALUE - channelReadBytes)
        {
            channelReadBytes = 0;
        }

        frameReadBytes += readableBytes;
        channelReadBytes += readableBytes;
        super.channelRead(ctx, msg);
    }

    @Monitor(name = "FrameThroughput(bytes_per_second)", type = DataSourceType.GAUGE)
    public long getFrameThroughput()
    {
        return frameReadBytes / SECONDS.convert(getFrameTimeElapsed(), MILLISECONDS);
    }

    @Monitor(name = "ChannelThroughput(bytes_per_second)", type = DataSourceType.GAUGE)
    public long getChannelThroughput()
    {
        return channelReadBytes / SECONDS.convert(getSessionTimeElapsed(), MILLISECONDS);
    }

    protected void timerCallback()
    {
        frameReadBytes = 0;
    }

    private long getSessionTimeElapsed()
    {
        return System.currentTimeMillis() - startDate.getTime();
    }

    private long getFrameTimeElapsed()
    {
        return getSessionTimeElapsed() % (MILLISECONDS.convert(getTimeFrame(), SECONDS));
    }

    public long getFrameReadBytes()
    {
        return frameReadBytes;
    }

    public long getChannelReadBytes()
    {
        return channelReadBytes;
    }
}
