package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseChannelHandlerTest
{
    @Mock
    protected HandlerConfig handlerConfig;
    @Mock
    protected ChannelHandlerContext channelHandlerContext;
    @Mock
    protected Channel channel;
    @Mock
    protected ChannelFuture channelFuture;
    @Mock
    protected ByteBuf msg;
    @Mock
    protected ChannelPromise promise;
    @Mock
    protected EventExecutor eventExecutor;

    @Before
    public void setup()
    {
        doReturn(channel).when(channelHandlerContext).channel();
        doReturn(eventExecutor).when(channelHandlerContext).executor();

        doReturn(channelFuture).when(channel).write(anyObject());
        doReturn(channelFuture).when(channel).writeAndFlush(anyObject());
    }
}

