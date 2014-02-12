package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.handler.BaseChannelHandlerTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ThroughputHandlerTest extends BaseChannelHandlerTest
{
    private ThroughputHandler handler;

    @Before
    public void setup()
    {
        super.setup();
        handler = spy(new ThroughputHandler(handlerConfig));
    }

    @Test
    public void shouldProcessReceivedMessage() throws Exception
    {
        final int cycleReadBytes = 32;
        final int messagesCount = 3;

        when(msg.readableBytes()).thenReturn(cycleReadBytes);
        for(int i = 1; i <= messagesCount; i++)
        {
            handler.updateThroughputMetric(msg);

            final long expectedBytes = i * cycleReadBytes;

            assertThat(expectedBytes, equalTo(handler.getFrameReadBytes()));
            assertThat(expectedBytes, equalTo(handler.getChannelBytes()));
        }
    }

    @Test
    public void shouldReturnFrameThroughput() throws Exception
    {
        final long bytesRead = 1024;
        final long frameTimeElapsed = 4000;

        doReturn(bytesRead).when(handler).getFrameReadBytes();
        doReturn(frameTimeElapsed).when(handler).getFrameTimeElapsed();

        assertThat(bytesRead * 1000 / frameTimeElapsed, equalTo(handler.getFrameThroughput()));
    }

    @Test
    public void shouldReturnChannelThroughput() throws Exception
    {
        final long bytesRead = 2048;
        final long sessionTimeElapsed = 5000;

        doReturn(bytesRead).when(handler).getChannelBytes();
        doReturn(sessionTimeElapsed).when(handler).getSessionTimeElapsed();

        assertThat(bytesRead * 1000 / sessionTimeElapsed, equalTo(handler.getChannelThroughput()));
    }
}
