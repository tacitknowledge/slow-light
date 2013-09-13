package com.tacitknowledge.slowlight.proxyserver.metrics;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
@ChannelHandler.Sharable
public class MetricsHandler extends ChannelDuplexHandler
{
    private final static AttributeKey<Long> START_TIME = new AttributeKey<>("attr.StartTime");

    private final MetricsHolder metrics;

    public MetricsHandler(final MetricsHolder metrics)
    {
        this.metrics = metrics;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.channel().attr(START_TIME).set(System.currentTimeMillis());
        metrics.counter.incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        metrics.bytesIn.addAndGet(((ByteBuf) msg).readableBytes());
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        metrics.bytesOut.addAndGet(((ByteBuf) msg).readableBytes());
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception
    {
        metrics.time.addAndGet(System.currentTimeMillis() - ctx.channel().attr(START_TIME).getAndRemove());
        super.channelInactive(ctx);
    }
}
