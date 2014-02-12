package com.tacitknowledge.slowlight.proxyserver.systest;

import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.server.Server;
import com.tacitknowledge.slowlight.proxyserver.server.proxy.ProxyServer;
import com.tacitknowledge.slowlight.proxyserver.systest.util.client.TestClient;
import com.tacitknowledge.slowlight.proxyserver.systest.util.server.TestServer;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class AbstractProxyServerIT
{
    protected static final int SERVER_PORT = 8080;

    private static int nextClientPort = 8081;

    protected int clientPort;

    @BeforeClass
    public static void setupClass() throws Exception
    {
        final TestServer testServer = new TestServer(SERVER_PORT);
        testServer.start();
    }

    @Before
    public void setup()
    {
        clientPort = nextPort();
    }

    protected Server createProxyServer(final List<HandlerConfig> handlerConfig) throws InterruptedException
    {
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setId("test proxy server");
        serverConfig.setType("proxy");
        serverConfig.setLocalPort(clientPort);
        serverConfig.setHandlers(handlerConfig);

        final Map<String, String> serverParams = new HashMap<String, String>() {{
            put("host", "localhost");
            put("port", Integer.toString(SERVER_PORT));
        }};
        serverConfig.setParams(serverParams);

        final Server server = new ProxyServer(serverConfig);
        server.start();

        return server;
    }

    protected TestClient createClient(final int responseSize) throws InterruptedException
    {
        final TestClient testClient = new TestClient(clientPort, responseSize);
        testClient.start();

        return testClient;
    }

    protected String convertResponseToString(final List<byte[]> responseBytesList)
    {
        byte[] response = new byte[0];

        for (final byte[] responseBytes : responseBytesList)
        {
            response = ArrayUtils.addAll(response, responseBytes);
        }

        return new String(response);
    }

    private static synchronized int nextPort()
    {
        return nextClientPort++;
    }
}
