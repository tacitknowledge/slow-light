package com.tacitknowledge.slowlight.proxyserver.server;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public abstract class AbstractServer implements Server
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServer.class);

    protected final ServerConfig serverConfig;

    public AbstractServer(final ServerConfig serverConfig)
    {
        this.serverConfig = serverConfig;
    }

    @Override
    public void start()
    {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(createBossGroup(), createWorkerGroup());
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.childHandler(createChannelInitializer());
        serverBootstrap.childOption(ChannelOption.AUTO_READ, false);

        serverBootstrap.bind(serverConfig.getLocalPort());

        LOG.info("Started {} server [{}] listening on port [{}]. Server parameters [{}]", serverConfig.getType(), serverConfig.getId(),
                serverConfig.getLocalPort(), serverConfig.getParams());
    }

    @Override
    public void shutdown()
    {
        // TODO: to be implemented
    }

    protected abstract EventLoopGroup createBossGroup();

    protected abstract EventLoopGroup createWorkerGroup();

    protected abstract ChannelInitializer createChannelInitializer();
}
