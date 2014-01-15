package com.tacitknowledge.slowlight.proxyserver.metrics;

import com.tacitknowledge.slowlight.proxyserver.handler.BaseChannelHandlerTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ExceptionCountHandlerTest extends BaseChannelHandlerTest
{
    private ExceptionCountHandler handler;

    @Before
    public void setup()
    {
        super.setup();
        handler = new ExceptionCountHandler(handlerConfig);
    }

    @Test
    public void shouldReturnExceptionCount() throws Exception
    {
        final int exceptionCount = 3;

        for(int i = 0; i < exceptionCount; i++)
        {
            handler.exceptionCaught(channelHandlerContext, new Throwable());
        }

        assertThat(handler.getExceptionCount(), is(equalTo(exceptionCount)));
    }
}
