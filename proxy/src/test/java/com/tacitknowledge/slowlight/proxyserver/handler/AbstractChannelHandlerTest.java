package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractChannelHandlerTest extends BaseChannelHandlerTest
{
    private class AbstractChannelHandlerTestable extends AbstractChannelHandler
    {
        public AbstractChannelHandlerTestable(HandlerConfig handlerConfig)
        {
            super(handlerConfig);
        }
    }

    private AbstractChannelHandlerTestable handler;

    @Before
    public void setup()
    {
        super.setup();
        handler = spy(new AbstractChannelHandlerTestable(handlerConfig));
    }

    @Test
    public void shouldCatchException() throws Exception
    {
        handler.exceptionCaught(channelHandlerContext, mock(Exception.class));
        verify(handler, times(1)).closeOnFlush(channel);
    }

    @Test
    public void shouldCloseOnFlush()
    {
        when(channel.isActive()).thenReturn(Boolean.TRUE);

        handler.closeOnFlush(channel);

        verify(channel, times(1)).writeAndFlush(Unpooled.EMPTY_BUFFER);
        verify(channelFuture, times(1)).addListener(ChannelFutureListener.CLOSE);
    }

    @Test
    public void shouldReturnTimeFrame()
    {
        final String timeFrameProp = "100";
        when(handlerConfig.getParam(AbstractChannelHandler.TIME_FRAME, false)).thenReturn(timeFrameProp);

        assertEquals(handler.getTimeFrame(), Long.parseLong(timeFrameProp));
    }

    @Test
    public void shouldReturnDefaultTimeFrame()
    {
        when(handlerConfig.getParam(AbstractChannelHandler.TIME_FRAME, false)).thenReturn(null);

        assertEquals(handler.getTimeFrame(), AbstractChannelHandler.ZERO_TIME_FRAME);
    }
}
