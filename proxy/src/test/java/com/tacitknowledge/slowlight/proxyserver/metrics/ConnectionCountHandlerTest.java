package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandlerTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ConnectionCountHandlerTest extends AbstractChannelHandlerTest
{
    private ConnectionCountHandler handler;

    @Before
    public void setup()
    {
        super.setup();
        handler = new ConnectionCountHandler(handlerConfig);
    }

    @Test
    public void shouldCountOpenConnections() throws Exception
    {
        final int connectionCount = 3;

        for(int i = 0; i < connectionCount; i++)
        {
            handler.channelActive(channelHandlerContext);
        }

        assertThat(handler.getOpenConnectionCount(), is(equalTo(connectionCount)));

        for(int i = 0; i < connectionCount; i++)
        {
            handler.channelInactive(channelHandlerContext);
        }

        assertThat(handler.getOpenConnectionCount(), is(equalTo(0)));
    }
}
