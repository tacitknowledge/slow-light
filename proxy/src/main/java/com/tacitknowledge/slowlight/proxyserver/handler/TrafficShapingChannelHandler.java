package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class TrafficShapingChannelHandler extends AbstractChannelHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(TrafficShapingChannelHandler.class);

    private static final String PARAM_WRITE_LIMIT = "writeLimit";
    private static final String PARAM_READ_LIMIT = "readLimit";

    // TODO: netty 4 traffic shaping handler seems to not work for large data, needs to be investigated
    private final ChannelTrafficShapingHandler channelTrafficShapingHandler;

    public TrafficShapingChannelHandler(final HandlerConfig handlerConfig)
    {
        super(handlerConfig);

        this.channelTrafficShapingHandler = new ChannelTrafficShapingHandler(
                Long.parseLong(handlerConfig.getParam(PARAM_WRITE_LIMIT)),
                Long.parseLong(handlerConfig.getParam(PARAM_READ_LIMIT))
        );
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        channelTrafficShapingHandler.channelRead(ctx, msg);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception
    {
        channelTrafficShapingHandler.read(ctx);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        channelTrafficShapingHandler.write(ctx, msg, promise);
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception
    {
        channelTrafficShapingHandler.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception
    {
        channelTrafficShapingHandler.handlerRemoved(ctx);
    }

    @Override
    public String toString()
    {
        return channelTrafficShapingHandler.toString();
    }

    @Override
    protected void timerCallback()
    {
        final long writeLimit = Long.parseLong(handlerConfig.getParam(PARAM_WRITE_LIMIT));
        final long readLimit = Long.parseLong(handlerConfig.getParam(PARAM_READ_LIMIT));

        channelTrafficShapingHandler.configure(writeLimit, readLimit);

        LOG.debug("write limit -> {}", writeLimit);
        LOG.debug("read limit -> {}", readLimit);
    }
}
