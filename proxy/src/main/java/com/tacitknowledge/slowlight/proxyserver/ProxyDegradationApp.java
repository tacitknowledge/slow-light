package com.tacitknowledge.slowlight.proxyserver;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ProxyDegradationApp
{
    public static void main(String[] args) throws InterruptedException
    {
        if(args.length != 1) {
            System.out.println("Please provide config");
            System.exit(-1);
        }

        File config = new File(args[0]);
        if(!config.exists()) {
            System.out.print("File doesn't exist");
            System.exit(-1);
        }

        ServersConfiguration serversConfiguration;
        try {
            XStream xStream = new XStream();
            serversConfiguration = (ServersConfiguration) xStream.fromXML(config);
        } catch (Exception e) {
            System.out.println("Can't read parse file");
            System.exit(-1);
            return;
        }

        List<ChannelFuture> futures = Lists.newArrayList();
        for (Server server : serversConfiguration.getServers())
        {
            ChannelFuture future = startServer(server);
            futures.add(future);
        }

        //wait until all servers finish
        for (ChannelFuture future : futures)
        {
            future.await();
        }
    }

    private static ChannelFuture startServer(Server server) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap()
                .group(bossGroup, workerGroup) //TODO configure?
                .channel(NioServerSocketChannel.class)
                .childHandler(new ScenarioChannellInitializer(server.getScenarios(), server.getScenarioSelector()))
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, false);

        System.out.println("Listen on " + server.getPort());

        return b.bind(server.getPort()).channel().closeFuture();
    }
}
