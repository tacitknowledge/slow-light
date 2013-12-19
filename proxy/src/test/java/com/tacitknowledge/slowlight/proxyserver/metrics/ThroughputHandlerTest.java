package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandlerTest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ThroughputHandlerTest extends AbstractChannelHandlerTest
{
    private final int cycleReadBytes = 32;
    private final int messagesCount = 3;

    private ThroughputHandler handler;

    @Before
    public void setup()
    {
        super.setup();

        when(handlerConfig.getParam(AbstractChannelHandler.TIME_FRAME, false)).thenReturn("10");
        when(msg.readableBytes()).thenReturn(cycleReadBytes);

        handler = new ThroughputHandler(handlerConfig);
    }

    @Test
    public void shouldProcessReceivedMessage() throws Exception
    {
        for(int i = 1; i <= messagesCount; i++)
        {
            handler.channelRead(channelHandlerContext, msg);

            final long expectedBytes = i * cycleReadBytes;

            assertThat(handler.getFrameReadBytes(), is(equalTo(expectedBytes)));
            assertThat(handler.getChannelReadBytes(), is(equalTo(expectedBytes)));
        }
    }

    @Test
    public void shouldReturnFrameThroughput() throws Exception
    {
        final long pause = 1000;

        for(int i = 0; i < messagesCount; i++)
        {
            handler.channelRead(channelHandlerContext, msg);
            Thread.sleep(pause);
        }

        final long expectedThroughput = cycleReadBytes  / TimeUnit.SECONDS.convert(pause, TimeUnit.MILLISECONDS);

        assertThat(handler.getFrameThroughput(), is(equalTo(expectedThroughput)));
    }

    @Test
    public void shouldReturnChannelThroughput() throws Exception
    {
        final long pause = 4000;

        for(int i = 0; i < messagesCount; i++)
        {
            handler.channelRead(channelHandlerContext, msg);
            Thread.sleep(pause);
        }

        final long expectedChannelThroughput = cycleReadBytes * messagesCount /
                TimeUnit.SECONDS.convert(pause * messagesCount, TimeUnit.MILLISECONDS);

        assertThat(handler.getChannelThroughput(), is(equalTo(expectedChannelThroughput)));
    }
}
