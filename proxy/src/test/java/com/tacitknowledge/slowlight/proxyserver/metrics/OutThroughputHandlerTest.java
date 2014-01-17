package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.handler.BaseChannelHandlerTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class OutThroughputHandlerTest extends BaseChannelHandlerTest
{
    @Test
    public void shouldUpdateThroughputOnWrite() throws Exception
    {
        final OutThroughputHandler handler =  spy(new OutThroughputHandler(handlerConfig));

        handler.write(channelHandlerContext, msg, promise);

        verify(handler, times(1)).updateThroughputMetric(msg);
        verify(channelHandlerContext, times(1)).write(msg, promise);
    }
}
