package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;

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
    public void shouldCreateChannelInitializer()
    {
        final ProxyChannelInitializer channelInitializer = (ProxyChannelInitializer) server.createChannelInitializer();

        assertThat(channelInitializer.getServerConfig(), is(serverConfig));
    }
}
