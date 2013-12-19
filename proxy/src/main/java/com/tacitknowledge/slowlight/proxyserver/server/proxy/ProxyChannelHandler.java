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

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
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
        if (targetChannel.isActive())
        {
            targetChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess())
                    {
                        ctx.channel().read();
                    }
                    else
                    {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        closeOnFlush(targetChannel);
    }

    private Channel connectTarget(final Channel sourceChannel)
    {
        final Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(clientWorkerGroup).channel(sourceChannel.getClass());
        clientBootstrap.option(ChannelOption.AUTO_READ, false);
        clientBootstrap.handler(new ProxyTargetChannelHandler(handlerConfig, sourceChannel));

        final ChannelFuture remoteChannelFuture = clientBootstrap.connect(new InetSocketAddress(targetHost, targetPort));

        remoteChannelFuture.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    sourceChannel.read();
                }
                else
                {
                    sourceChannel.close();
                }
            }
        });

        return remoteChannelFuture.channel();
    }
}
