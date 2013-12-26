package com.tacitknowledge.slowlight.proxyserver.systest.util.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class TestServer
{
    private final int port;
    private final ServerBootstrap serverBootstrap;

    public TestServer(final int port)
    {
        this.port = port;

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ServerChannelHandler());
        serverBootstrap.childOption(ChannelOption.AUTO_READ, false);
    }

    public void start() throws InterruptedException
    {
        final ChannelFuture channel = serverBootstrap.bind(port);
        channel.await();
    }
}
