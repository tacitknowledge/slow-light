package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * This handler class provides the implementation of proxy channel handler - server side.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ProxyChannelHandler extends AbstractChannelHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(ProxyChannelHandler.class);
    private String targetHost;
    private int targetPort;
    private Channel targetChannel;
    private EventLoopGroup clientWorkerGroup;

    public ProxyChannelHandler(final HandlerConfig handlerConfig, final String targetHost, final int targetPort,
                               final EventLoopGroup clientWorkerGroup)
    {
        super(handlerConfig);

        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.clientWorkerGroup = clientWorkerGroup;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        targetChannel = connectTarget(ctx.channel());
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        if (getTargetChannel().isActive())
        {
            final ChannelFutureListener channelFutureListener = getChannelFutureListener(ctx.channel());
            getTargetChannel().writeAndFlush(msg).addListener(channelFutureListener);
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception
    {
        closeOnFlush(getTargetChannel());
    }

    protected Channel connectTarget(final Channel sourceChannel)
    {
        final Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(clientWorkerGroup).channel(sourceChannel.getClass());
        clientBootstrap.option(ChannelOption.AUTO_READ, false);
        clientBootstrap.handler(new ProxyTargetChannelHandler(handlerConfig, sourceChannel));

        final ChannelFuture remoteChannelFuture = clientBootstrap.connect(new InetSocketAddress(targetHost, targetPort));

        final ChannelFutureListener channelFutureListener = getChannelFutureListener(sourceChannel);
        remoteChannelFuture.addListener(channelFutureListener);

        return remoteChannelFuture.channel();
    }

    protected ChannelFutureListener getChannelFutureListener(final Channel channel)
    {
        return new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    channel.read();
                }
                else
                {
                    channel.close();
                }
            }
        };
    }

    public String getTargetHost()
    {
        return targetHost;
    }

    public int getTargetPort()
    {
        return targetPort;
    }

    public EventLoopGroup getClientWorkerGroup()
    {
        return clientWorkerGroup;
    }

    public Channel getTargetChannel()
    {
        return targetChannel;
    }
}
