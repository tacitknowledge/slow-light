package com.tacitknowledge.slowlight.proxyserver.handler;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DiscardChannelHandlerTest extends BaseChannelHandlerTest
{
    private DiscardChannelHandler discardChannelHandler;

    @Before
    public void setup()
    {
        super.setup();

        discardChannelHandler = new DiscardChannelHandler(handlerConfig);
    }

    @Test
    public void handlerShouldEnableReadOnChannelActivation() throws Exception
    {
        discardChannelHandler.channelActive(channelHandlerContext);

        verify(channelHandlerContext).read();
    }

    @Test
    public void handlerShouldDiscardDataAndEnableNextRead() throws Exception
    {
        discardChannelHandler.channelRead(channelHandlerContext, msg);

        verify(channelHandlerContext).read();
        verifyNoMoreInteractions(channelHandlerContext);
    }
}
