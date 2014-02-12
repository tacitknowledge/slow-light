package com.tacitknowledge.slowlight.proxyserver.systest.util.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
* @author Alexandr Donciu (adonciu@tacitknowledge.com)
*/
public class TestClient
{
    private static final String LOCALHOST = "localhost";

    private final int port;

    private final int responseSize;

    private final Lock responseLock = new ReentrantLock();
    private final Condition responseCondition = responseLock.newCondition();
    private final ServerResponse response = new ServerResponse();

    private ChannelFuture channel;

    public TestClient(final int port, final int responseSize)
    {
        this.port = port;

        this.responseSize = responseSize;
    }

    public void start() throws InterruptedException
    {
        channel = connect();
        channel.await();
    }

    public List<byte[]> sendMessage(final String message) throws Throwable
    {
        return sendMessage(message, 0);
    }

    public List<byte[]> sendMessage(final String message, final long timeout) throws Throwable
    {
        if (channel == null)
        {
            throw new NullPointerException("Got null channel, it seems that test client is not started yet");
        }

        response.reset();

        final ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getBytes());
        final ChannelFuture future = channel.channel().writeAndFlush(byteBuf);
        future.await();

        List<byte[]> responseBytesList;
        if (future.isSuccess())
        {
            responseBytesList = getResponse(responseLock, responseCondition, response, timeout);
        }
        else
        {
            throw future.cause();
        }

        return responseBytesList;
    }

    private ChannelFuture connect()
    {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientChannelHandler(responseLock, responseCondition, response, responseSize));
        bootstrap.option(ChannelOption.AUTO_READ, false);

        return bootstrap.connect(LOCALHOST, port);
    }

    private List<byte[]> getResponse(final Lock responseLock,
                                     final Condition responseReady,
                                     final ServerResponse response,
                                     final long timeout)
            throws InterruptedException
    {
        responseLock.lock();
        try
        {
            if (response.size() < responseSize)
            {
                if (timeout > 0)
                {
                    responseReady.await(timeout, TimeUnit.MILLISECONDS);
                }
                else
                {
                    responseReady.await();
                }
            }
        }
        finally
        {
            responseLock.unlock();
        }

        return response.get();
    }
}
