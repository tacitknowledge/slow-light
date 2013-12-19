package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class LogChannelHandlerTest extends AbstractChannelHandlerTest
{
    private LogChannelHandler handler;

    @Before
    public void setup()
    {
        handler = new LogChannelHandler(new HandlerConfig());
    }

    @Test
    public void shouldWrite() throws Exception
    {
        handler.write(channelHandlerContext, msg, promise);
        verify(channelHandlerContext, times(1)).write(msg, promise);
    }

    @Test
    public void shouldReadChannel() throws Exception
    {
        handler.channelRead(channelHandlerContext, msg);
        verify(channelHandlerContext, times(1)).fireChannelRead(msg);
    }
}
