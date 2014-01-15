package com.tacitknowledge.slowlight.proxyserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DelayChannelHanderTest extends BaseChannelHandlerTest
{
    @Test
    public void handlerShouldSplitDataIntoFragmentsAndDelayResponse() throws Exception
    {
        final String messageContent = "message data to be sent";

        final int dataSize = 3;
        final long delay = 100;

        doReturn(Integer.toString(dataSize)).when(handlerConfig).getParam(DelayChannelHandler.PARAM_MAX_DATA_SIZE);
        doReturn(Long.toString(delay)).when(handlerConfig).getParam(DelayChannelHandler.PARAM_DELAY);

        final ByteBuf message = Unpooled.wrappedBuffer(messageContent.getBytes());

        final DelayChannelHandler delayChannelHandler = new DelayChannelHandler(handlerConfig);

        delayChannelHandler.write(channelHandlerContext, message, promise);

        verify(eventExecutor).schedule((Runnable) Matchers.anyObject(), Matchers.eq(delay), Matchers.eq(TimeUnit.MILLISECONDS));
    }
}
