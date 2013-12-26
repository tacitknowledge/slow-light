package com.tacitknowledge.slowlight.proxyserver.server;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class DynamicChannelInitializerTest
{
    @Mock
    private ServerConfig serverConfig;

    @Mock
    private Channel channel;

    @Mock
    private ChannelPipeline pipeline;

    private DynamicChannelInitializer dynamicChannelInitializer;

    @Before
    public void setup()
    {
        doReturn(pipeline).when(channel).pipeline();

        dynamicChannelInitializer = spy(new DynamicChannelInitializer(serverConfig));
    }

    @Test
    public void channelInitializerShouldAddConfiguredHandlersToPipeline() throws Exception
    {
        final HandlerConfig handlerConfig1 = mock(HandlerConfig.class);
        final HandlerConfig handlerConfig2 = mock(HandlerConfig.class);
        doReturn("logHandler").when(handlerConfig1).getName();
        doReturn("delayHandler").when(handlerConfig2).getName();

        doReturn(Arrays.asList(handlerConfig1, handlerConfig2)).when(serverConfig).getHandlers();

        final AbstractChannelHandler handler1 = mock(AbstractChannelHandler.class);
        final AbstractChannelHandler handler2 = mock(AbstractChannelHandler.class);
        doReturn(handler1).when(dynamicChannelInitializer).createChannelHandler(handlerConfig1);
        doReturn(handler2).when(dynamicChannelInitializer).createChannelHandler(handlerConfig2);

        dynamicChannelInitializer.initChannel(channel);

        final InOrder inOrder = inOrder(pipeline);

        inOrder.verify(pipeline).addFirst(handlerConfig2.getName(), handler2);
        inOrder.verify(pipeline).addFirst(handlerConfig1.getName(), handler1);
    }
}
