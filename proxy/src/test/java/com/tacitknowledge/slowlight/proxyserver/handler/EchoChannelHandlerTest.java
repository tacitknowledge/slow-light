package com.tacitknowledge.slowlight.proxyserver.handler;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class EchoChannelHandlerTest extends BaseChannelHandlerTest
{
    private EchoChannelHandler echoChannelHandler;

    @Before
    public void setup()
    {
        super.setup();

        echoChannelHandler = new EchoChannelHandler(handlerConfig);
    }

    @Test
    public void handlerShouldEnableReadOnChannelActivation() throws Exception
    {
        echoChannelHandler.channelActive(channelHandlerContext);

        verify(channelHandlerContext).read();
    }

    @Test
    public void handlerShouldWriteRequestToResponse() throws Exception
    {
        echoChannelHandler.channelRead(channelHandlerContext, msg);

        verify(channel).writeAndFlush(msg);
    }
}
