package com.tacitknowledge.slowlight.proxyserver.systest;

import com.google.common.collect.Lists;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.DiscardChannelHandler;
import com.tacitknowledge.slowlight.proxyserver.systest.util.client.TestClient;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DiscardProxyServerIT extends AbstractProxyServerIT
{
    @Test
    public void proxyServerShouldDiscardRequestDataAndNotRespond() throws Throwable
    {
        createProxyServer(createDiscardHandlerConfig());
        final TestClient client = createClient(10);

        final String request = "1234567890";
        final List<byte[]> responseBytesList = client.sendMessage(request, 1000);

        assertThat(responseBytesList, is(empty()));
    }

    private List<HandlerConfig> createDiscardHandlerConfig()
    {
        final HandlerConfig discardHandlerConfig = new HandlerConfig();
        discardHandlerConfig.setName("delayHandler");
        discardHandlerConfig.setType(DiscardChannelHandler.class.getName());

        return Lists.newArrayList(discardHandlerConfig);
    }
}
