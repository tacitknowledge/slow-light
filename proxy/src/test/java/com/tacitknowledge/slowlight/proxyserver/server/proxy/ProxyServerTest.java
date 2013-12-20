package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import io.netty.channel.EventLoopGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ProxyServerTest
{
    @Mock
    private ServerConfig serverConfig;

    private ProxyServer server;

    @Before
    public void setup()
    {
        server = spy(new ProxyServer(serverConfig));
    }

    @Test
    public void shouldCreateBossGroup()
    {
        final EventLoopGroup eventLoopGroup = server.createBossGroup();

        assertThat(eventLoopGroup, notNullValue());
    }

    @Test
    public void shouldCreateWorkerGroup()
    {
        final EventLoopGroup eventLoopGroup = server.createWorkerGroup();

        assertThat(eventLoopGroup, notNullValue());
    }

    @Test
    public void shouldCreateClientWorkerGroup()
    {
        final EventLoopGroup eventLoopGroup = server.createClientWorkerGroup();

        assertThat(eventLoopGroup, notNullValue());
    }

    @Test
    public void shouldCreateChannelInitializer()
    {
        final EventLoopGroup clientWorkerGroup = mock(EventLoopGroup.class);

        doReturn(clientWorkerGroup).when(server).createClientWorkerGroup();

        final ProxyChannelInitializer channelInitializer = (ProxyChannelInitializer) server.createChannelInitializer();

        assertThat(channelInitializer.getServerConfig(), is(serverConfig));
        assertThat(channelInitializer.getClientWorkerGroup(), is(clientWorkerGroup));
    }
}
