package com.tacitknowledge.slowlight.proxyserver.server.proxy;

import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandlerTest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ProxyTargetChannelHandlerTest extends AbstractChannelHandlerTest
{
    @Mock
    private Channel sourceChannel;
    private ProxyTargetChannelHandler handler;

    @Before
    public void setup()
    {
        handler = spy(new ProxyTargetChannelHandler(handlerConfig, sourceChannel));
    }

    @Test
    public void shouldBeProperlyInitialized()
    {
        assertThat(handlerConfig, is(handler.getHandlerConfig()));
        assertThat(sourceChannel, is(handler.getSourceChannel()));
    }

    @Test
    public void shouldActivateChannel() throws Exception
    {
        handler.channelActive(channelHandlerContext);

        verify(channelHandlerContext, times(1)).read();
    }

    @Test
    public void shouldReadChannel() throws Exception
    {
        final ChannelFutureListener channelFutureListener = mock(ChannelFutureListener.class);

        when(sourceChannel.isActive()).thenReturn(true);
        when(sourceChannel.writeAndFlush(msg)).thenReturn(channelFuture);
        doReturn(channelFutureListener).when(handler).getChannelFutureListener(channelHandlerContext);

        handler.channelRead(channelHandlerContext, msg);

        verify(sourceChannel, times(1)).writeAndFlush(msg);
        verify(channelFuture, times(1)).addListener(channelFutureListener);
    }

    @Test
    public void shouldInactivateChannel() throws Exception
    {
        handler.channelInactive(channelHandlerContext);

        verify(handler, times(1)).closeOnFlush(sourceChannel);
    }
}
