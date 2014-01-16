package com.tacitknowledge.slowlight.proxyserver.handler;

import io.netty.channel.Channel;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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

    @Test
    public void listenerShouldRequestChannelRead() throws Exception
    {
        doReturn(true).when(channelFuture).isSuccess();

        final EchoChannelHandler.EchoMessageListener listener = echoChannelHandler.new EchoMessageListener(channelHandlerContext);
        listener.operationComplete(channelFuture);

        verify(channel).read();
    }

    @Test
    public void listenerShouldCloseChannelIfWriteFutureCompletedUnsuccessfully() throws Exception
    {
        doReturn(false).when(channelFuture).isSuccess();

        final Channel futureChannel = mock(Channel.class);
        doReturn(futureChannel).when(channelFuture).channel();

        final EchoChannelHandler.EchoMessageListener listener = echoChannelHandler.new EchoMessageListener(channelHandlerContext);
        listener.operationComplete(channelFuture);

        verify(futureChannel).close();
    }
}
