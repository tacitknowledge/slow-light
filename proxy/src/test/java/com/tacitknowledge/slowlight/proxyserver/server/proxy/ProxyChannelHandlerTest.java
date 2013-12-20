package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandlerTest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ProxyChannelHandlerTest extends AbstractChannelHandlerTest
{
    private final String host = "host";
    private final int port = 8000;
    @Mock
    private EventLoopGroup clientWorkerGroup;
    private ProxyChannelHandler handler;

    @Before
    public void setup()
    {
        super.setup();
        handler = spy(new ProxyChannelHandler(handlerConfig, host, port, clientWorkerGroup));
    }

    @Test
    public void shouldBeProperlyInitialized()
    {
        assertThat(handler.getHandlerConfig(), is(handlerConfig));
        assertThat(handler.getTargetHost(), is(host));
        assertThat(handler.getTargetPort(), is(port));
        assertThat(handler.getClientWorkerGroup(), is(clientWorkerGroup));
    }

    @Test
    public void shouldActivateChannel() throws Exception
    {
        final Channel targetChannel = mock(Channel.class);

        doReturn(targetChannel).when(handler).connectTarget(channel);

        handler.channelActive(channelHandlerContext);

        assertThat(handler.getTargetChannel(), is(targetChannel));
    }

    @Test
    public void shouldReadChannel() throws Exception
    {
        final ChannelFutureListener channelFutureListener = mock(ChannelFutureListener.class);

        when(channel.isActive()).thenReturn(true);
        doReturn(channelFutureListener).when(handler).getChannelFutureListener(channel);
        doReturn(channel).when(handler).getTargetChannel();

        handler.channelRead(channelHandlerContext, msg);

        verify(channel, times(1)).writeAndFlush(msg);
        verify(channelFuture, times(1)).addListener(channelFutureListener);
    }

    @Test
    public void shouldInactivateChannel() throws Exception
    {
        doReturn(channel).when(handler).getTargetChannel();

        handler.channelInactive(channelHandlerContext);

        verify(handler, times(1)).closeOnFlush(channel);
    }
}
