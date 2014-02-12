package com.tacitknowledge.slowlight.proxyserver.systest;

import com.google.common.collect.Lists;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.RandomDataChannelHandler;
import com.tacitknowledge.slowlight.proxyserver.systest.util.client.TestClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class RandomDataProxyServerIT extends AbstractProxyServerIT
{
    @Test
    public void proxyServerShouldGenerateRandomDataBySpecifiedParams() throws Throwable
    {
        createProxyServer(createRandomDataHandlerConfig(3, 5));
        final TestClient client = createClient(15);

        final String request = "1234567890";
        final List<byte[]> responseBytesList = client.sendMessage(request);

        final String response = convertResponseToString(responseBytesList);
        assertThat(response.length(), is(equalTo(15)));
    }

    private List<HandlerConfig> createRandomDataHandlerConfig(final int dataFragments, final int dataFragmentSize)
    {
        final HandlerConfig randomDataHandlerConfig = new HandlerConfig();
        randomDataHandlerConfig.setName("randomDataHandler");
        randomDataHandlerConfig.setType(RandomDataChannelHandler.class.getName());

        final Map<String, String> handlerParam = new HashMap<String, String>() {{
            put("dataFragments", Integer.toString(dataFragments));
            put("dataFragmentSize", Integer.toString(dataFragmentSize));
        }};
        randomDataHandlerConfig.setParams(handlerParam);

        return Lists.newArrayList(randomDataHandlerConfig);
    }
}
