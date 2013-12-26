package com.tacitknowledge.slowlight.proxyserver.systest;

import com.google.common.collect.Lists;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.CloseConnectionChannelHandler;
import com.tacitknowledge.slowlight.proxyserver.systest.util.client.TestClient;
import org.junit.Test;

import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class CloseConnectionProxyServerIT extends AbstractProxyServerIT
{
    @Test(expected = ClosedChannelException.class)
    public void proxyServerShouldCloseConnectionImmediately() throws Throwable
    {
        createProxyServer(getCloseConnectionHandlerConfigs(0));
        final TestClient client = createClient(10);

        Thread.sleep(300);

        client.sendMessage("1234567890");
    }

    @Test(timeout = 2000, expected = ClosedChannelException.class)
    public void proxyServerShouldCloseConnectionAfterGivenTimeout() throws Throwable
    {
        createProxyServer(getCloseConnectionHandlerConfigs(1000));
        final TestClient client = createClient(10);

        final String request = "1234567890";
        final List<byte[]> responseBytesList = client.sendMessage(request);
        assertThat(convertResponseToString(responseBytesList), is(equalTo(request)));

        Thread.sleep(1500);

        client.sendMessage("1234567890");
    }

    private List<HandlerConfig> getCloseConnectionHandlerConfigs(final long closeConnectionAfter)
    {
        final HandlerConfig handlerConfig = new HandlerConfig();
        handlerConfig.setName("closeConnectionHandler");
        handlerConfig.setType(CloseConnectionChannelHandler.class.getName());

        final Map<String, String> handlerParam = new HashMap<String, String>() {{
            put("closeConnectionAfter", Long.toString(closeConnectionAfter));
        }};
        handlerConfig.setParams(handlerParam);

        return Lists.newArrayList(handlerConfig);
    }
}
