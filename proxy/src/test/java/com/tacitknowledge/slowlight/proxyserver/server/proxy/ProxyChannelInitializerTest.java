package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.tacitknowledge.slowlight.proxyserver.server.proxy.ProxyChannelInitializer.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ProxyChannelInitializerTest
{
    @Mock
    private EventLoopGroup clientWorkerGroup;
    @Mock
    private Channel channel;
    @Mock
    private ChannelConfig channelConfig;
    @Mock
    private ServerConfig serverConfig;
    @Mock
    private ChannelPipeline pipeline;

    private ProxyChannelInitializer initializer;

    @Before
    public void setup()
    {
        when(channel.config()).thenReturn(channelConfig);
        when(channel.pipeline()).thenReturn(pipeline);
        when(serverConfig.getParam(PARAM_HOST)).thenReturn("host");
        when(serverConfig.getParam(PARAM_PORT)).thenReturn("8000");

        initializer = new ProxyChannelInitializer(serverConfig, clientWorkerGroup);
    }

    @Test
    public void shouldInitializeChannel() throws Exception
    {
        ArgumentCaptor<String> handlerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProxyChannelHandler> handler = ArgumentCaptor.forClass(ProxyChannelHandler.class);

        initializer.initChannel(channel);

        verify(channelConfig, times(1)).setAutoRead(false);
        verify(pipeline).addLast(handlerNameCaptor.capture(), handler.capture());

        assertThat(PROXY_HANDLER_NAME, equalTo(handlerNameCaptor.getValue()));

        final String actualHost = handler.getValue().getTargetHost();
        final String actualPort = String.valueOf(handler.getValue().getTargetPort());

        assertThat(serverConfig.getParam(PARAM_HOST), equalTo(actualHost));
        assertThat(serverConfig.getParam(PARAM_PORT), equalTo(actualPort));
    }
}
