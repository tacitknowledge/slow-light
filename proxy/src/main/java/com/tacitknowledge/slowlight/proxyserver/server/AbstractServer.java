package com.tacitknowledge.slowlight.proxyserver.server;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public abstract class AbstractServer implements Server
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServer.class);

    protected static final String PARAM_CONNECTION_THREADS = "connectionThreads";
    protected static final String PARAM_WORKER_THREADS = "workerThreads";

    protected final ServerConfig serverConfig;

    public AbstractServer(final ServerConfig serverConfig)
    {
        this.serverConfig = serverConfig;
    }

    @Override
    public void start()
    {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(createEventLoopGroup(PARAM_CONNECTION_THREADS), createEventLoopGroup(PARAM_WORKER_THREADS));
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

    protected EventLoopGroup createEventLoopGroup(final String numberOfThreadsParam)
    {
        final String numberOfThreads = serverConfig.getParam(numberOfThreadsParam, false);
        Integer numberOfThreadsValue = (numberOfThreads != null ? Integer.valueOf(numberOfThreads) : null);
        if (numberOfThreadsValue == null || numberOfThreadsValue <= 0)
        {
            numberOfThreadsValue = 0;
        }

        return new NioEventLoopGroup(numberOfThreadsValue);
    }

    protected abstract ChannelInitializer createChannelInitializer();
}
