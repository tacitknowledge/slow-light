package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.handler.BaseChannelHandlerTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class InThroughputHandlerTest extends BaseChannelHandlerTest
{
    @Test
    public void shouldUpdateThroughputOnRead() throws Exception
    {
        final InThroughputHandler handler = spy(new InThroughputHandler(handlerConfig));

        handler.channelRead(channelHandlerContext, msg);

        verify(handler, times(1)).updateThroughputMetric(msg);
        verify(channelHandlerContext, times(1)).fireChannelRead(msg);
    }
}
