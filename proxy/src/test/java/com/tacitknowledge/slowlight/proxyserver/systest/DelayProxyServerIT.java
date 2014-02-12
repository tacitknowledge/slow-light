package com.tacitknowledge.slowlight.proxyserver.systest;

import com.google.common.collect.Lists;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.DelayChannelHandler;
import com.tacitknowledge.slowlight.proxyserver.systest.util.client.TestClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DelayProxyServerIT extends AbstractProxyServerIT
{
    @Test(timeout = 2500)
    public void proxyServerShouldSplitAndDelayResponseBySpecifiedParams() throws Throwable
    {
        createProxyServer(getDelayHandlerConfigs(5, 500));

        final String request = "12345678901234567890";
        final TestClient client = createClient(20);

        long startTime = System.currentTimeMillis();
        final List<byte[]> response = client.sendMessage(request);
        long endTime = System.currentTimeMillis();

        assertThat(endTime - startTime, is(greaterThan(2000L)));
        assertThat(convertResponseToString(response), is(equalTo(request)));

        for (final byte[] responseBytes : response)
        {
            assertThat(responseBytes.length, is(lessThanOrEqualTo(5)));
        }
    }

    @Test(timeout = 1000)
    public void proxyServerShouldNotDelayResponseIfDelayParamIsSetToZero() throws Throwable
    {
        createProxyServer(getDelayHandlerConfigs(5, 0));

        final String request = "12345678901234567890";
        final TestClient client = createClient(20);

        client.sendMessage(request);
    }

    @Test
    public void proxyServerShouldNotSplitResponseIfMaxDataSizeParamIsSetToZero() throws Throwable
    {
        createProxyServer(getDelayHandlerConfigs(0, 0));

        final String request = "12345678901234567890";
        final TestClient client = createClient(20);

        final List<byte[]> response = client.sendMessage(request);

        for (final byte[] responseBytes : response)
        {
            assertThat(responseBytes.length, is(greaterThan(0)));
        }
    }

    private List<HandlerConfig> getDelayHandlerConfigs(final int dataSize, final long delay)
    {
        final HandlerConfig delayHandlerConfig = new HandlerConfig();
        delayHandlerConfig.setName("delayHandler");
        delayHandlerConfig.setType(DelayChannelHandler.class.getName());

        final Map<String, String> handlerParam = new HashMap<String, String>() {{
            put("maxDataSize", Integer.toString(dataSize));
            put("delay", Long.toString(delay));
        }};
        delayHandlerConfig.setParams(handlerParam);

        return Lists.newArrayList(delayHandlerConfig);
    }
}
