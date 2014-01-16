package com.tacitknowledge.slowlight.proxyserver.handler;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class CloseConnectionChannelHandlerTest extends BaseChannelHandlerTest
{
    @Before
    public void setup()
    {
        super.setup();

        doReturn(Long.toString(0L)).when(handlerConfig).getParam("closeConnectionAfter");
    }

    @Test
    public void handlerShouldScheduleCloseConnectionOnChannelActivation() throws Exception
    {
        final long timeout = 100L;
        doReturn(Long.toString(timeout)).when(handlerConfig).getParam("closeConnectionAfter");

        final CloseConnectionChannelHandler closeConnectionChannelHandler = new CloseConnectionChannelHandler(handlerConfig);

        closeConnectionChannelHandler.channelActive(channelHandlerContext);

        verify(eventExecutor).schedule((Runnable) anyObject(), eq(timeout), eq(TimeUnit.MILLISECONDS));
        verify(channelHandlerContext).fireChannelActive();
    }

    @Test
    public void handlerShouldCloseChannelConnectionImmediatelyWhenCloseConnectionAfterSetToZero() throws Exception
    {
        doReturn("0").when(handlerConfig).getParam("closeConnectionAfter");

        final CloseConnectionChannelHandler closeConnectionChannelHandler = new CloseConnectionChannelHandler(handlerConfig);

        closeConnectionChannelHandler.channelActive(channelHandlerContext);

        verify(channel).close();
        verifyNoMoreInteractions(eventExecutor);
    }

    @Test
    public void handlerShouldFireChannelReadForNextHandler() throws Exception
    {
        final CloseConnectionChannelHandler closeConnectionChannelHandler = new CloseConnectionChannelHandler(handlerConfig);

        closeConnectionChannelHandler.channelRead(channelHandlerContext, msg);

        verify(channelHandlerContext).fireChannelRead(msg);
    }

    @Test
    public void runnableShouldCloseChannelConnection()
    {
        final CloseConnectionChannelHandler closeConnectionChannelHandler = new CloseConnectionChannelHandler(handlerConfig);
        final CloseConnectionChannelHandler.CloseConnectionRunnable closeConnectionRunnable = closeConnectionChannelHandler
            .new CloseConnectionRunnable(channelHandlerContext);

        closeConnectionRunnable.run();

        verify(channel).close();
    }
}
