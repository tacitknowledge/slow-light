package com.tacitknowledge.slowlight.proxyserver.server.simple;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.DynamicChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleServerTest
{
    private SimpleServer server;

    @Before
    public void setup()
    {
        server = new SimpleServer(new ServerConfig());
    }

    @Test
    public void shouldCreateBossGroup()
    {
         assertThat((NioEventLoopGroup) server.createBossGroup(), notNullValue());
    }

    @Test
    public void shouldCreateWorkerGroup()
    {
         assertThat((NioEventLoopGroup) server.createWorkerGroup(), notNullValue());
    }

    public void shouldCreateInitializer()
    {
        assertThat((DynamicChannelInitializer) server.createChannelInitializer(), notNullValue());
    }
}
