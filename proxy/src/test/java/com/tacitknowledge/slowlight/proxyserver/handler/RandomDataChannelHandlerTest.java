package com.tacitknowledge.slowlight.proxyserver.handler;

import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class RandomDataChannelHandlerTest extends BaseChannelHandlerTest
{
    @Test
    public void handlerShouldRespondWithRandomData() throws Exception
    {
        final int dataFragments = 100;
        final int dataFragmentSize = 3;

        doReturn(Integer.toString(dataFragments)).when(handlerConfig).getParam(RandomDataChannelHandler.PARAM_DATA_FRAGMENTS);
        doReturn(Integer.toString(dataFragmentSize)).when(handlerConfig).getParam(RandomDataChannelHandler.PARAM_DATA_FRAGMENT_SIZE);

        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);

        randomDataChannelHandler.channelRead(channelHandlerContext, msg);

        verify(eventExecutor).schedule((Runnable) Matchers.anyObject(), eq(0L), eq(TimeUnit.MILLISECONDS));
    }
}
