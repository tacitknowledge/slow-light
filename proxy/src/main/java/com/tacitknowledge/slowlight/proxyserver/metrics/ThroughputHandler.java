package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.buffer.ByteBuf;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link io.netty.channel.ChannelHandler} implementation that computes
 * throughput as bytes per second. The metric is determined at two levels :
 *
 *  <b>Frame</b>  - inside a specified time frame
 *  <b>Channel</b> - throughput determined per test session
 *
 *  See also :
 *
 *      {@link com.tacitknowledge.slowlight.proxyserver.metrics.InThroughputHandler}
 *
 *      and
 *
 *      {@link com.tacitknowledge.slowlight.proxyserver.metrics.OutThroughputHandler}
 *
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ThroughputHandler extends AbstractChannelHandler
{
    public static final TimeUnit SECONDS = TimeUnit.SECONDS;
    public static final TimeUnit MILLISECONDS = TimeUnit.MILLISECONDS;

    private Date startDate = new Date();
    private AtomicLong frameReadBytes = new AtomicLong(0);
    private AtomicLong channelBytes = new AtomicLong(0);

    public ThroughputHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);

        Monitors.registerObject(handlerConfig.getName(), this);
    }

    protected void updateThroughputMetric(final Object msg)
    {
        final long readableBytes = ((ByteBuf) msg).readableBytes();

        if(readableBytes > Long.MAX_VALUE - channelBytes.longValue())
        {
            channelBytes.getAndSet(0);
        }

        frameReadBytes.getAndAdd(readableBytes);
        channelBytes.getAndAdd(readableBytes);
    }

    @Monitor(name = "FrameThroughput(bytes_per_second)", type = DataSourceType.GAUGE)
    public long getFrameThroughput()
    {
        return getFrameReadBytes() / SECONDS.convert(getFrameTimeElapsed(), MILLISECONDS);
    }

    @Monitor(name = "ChannelThroughput(bytes_per_second)", type = DataSourceType.GAUGE)
    public long getChannelThroughput()
    {
        return getChannelBytes() / SECONDS.convert(getSessionTimeElapsed(), MILLISECONDS);
    }

    protected void timerCallback()
    {
        frameReadBytes.getAndSet(0);
    }

    public long getSessionTimeElapsed()
    {
        return System.currentTimeMillis() - startDate.getTime();
    }

    public long getFrameTimeElapsed()
    {
        return getSessionTimeElapsed() % (MILLISECONDS.convert(getTimeFrame(), SECONDS));
    }

    public long getFrameReadBytes()
    {
        return frameReadBytes.longValue();
    }

    public long getChannelBytes()
    {
        return channelBytes.longValue();
    }
}
