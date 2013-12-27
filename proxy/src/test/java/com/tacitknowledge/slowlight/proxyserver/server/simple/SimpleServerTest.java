package com.tacitknowledge.slowlight.proxyserver.server.simple;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
    public void shouldCreateInitializer()
    {
        assertThat(server.createChannelInitializer(), is(notNullValue()));
    }
}
