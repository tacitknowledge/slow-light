package com.tacitknowledge.slowlight.proxyserver.server.simple;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.Socket;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void shouldStartServer() throws Exception
    {
        final Integer localPort = 9090;
        final ServerConfig config = mock(ServerConfig.class);

        when(config.getLocalPort()).thenReturn(localPort);
        when(config.getHandlers()).thenReturn(Collections.<HandlerConfig>emptyList());

        final SimpleServer server = new SimpleServer(config);

        server.start();

        Socket client = null;
        try
        {
            client = new Socket("localhost", localPort);
            assertTrue(client.isConnected());
        }
        catch (Exception ex)
        {
            fail("unable to create connection");
        }
        finally
        {
            if(client != null)
            {
                client.close();
            }
        }
    }
}
