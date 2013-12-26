package com.tacitknowledge.slowlight.proxyserver.server;

import com.tacitknowledge.slowlight.proxyserver.config.ConfigBuilder;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class SlowlightServerTest
{
    private static final String configFileName = "the.config";

    @Mock
    private ConfigBuilder configBuilder;

    @Spy
    @InjectMocks
    private final SlowlightServer slowlightServer = new SlowlightServer();

    @Test(expected = IllegalArgumentException.class)
    public void serverShouldThrowExceptionIfNotAllRequiredArgumentsWereSpecified() throws Exception
    {
        slowlightServer.start(new String[] {});
    }

    @Test(expected = RuntimeException.class)
    public void serverShouldThrowExceptionIfCannotInstantiateServer() throws Exception
    {
        final ServerConfig serverConfig = mock(ServerConfig.class);

        final SlowlightConfig mainConfig = mock(SlowlightConfig.class);
        doReturn(Arrays.asList(serverConfig)).when(mainConfig).getServers();

        doReturn(mainConfig).when(configBuilder).getConfig(configFileName);

        doThrow(new RuntimeException()).when(slowlightServer).start(new String[] {configFileName});

        slowlightServer.start(new String[] {configFileName});
    }

    @Test
    public void serverShouldGetConfigurationAndStartAllServers() throws Exception
    {
        final ServerConfig serverConfig1 = mock(ServerConfig.class);
        final ServerConfig serverConfig2 = mock(ServerConfig.class);

        final SlowlightConfig mainConfig = mock(SlowlightConfig.class);
        doReturn(Arrays.asList(serverConfig1, serverConfig2)).when(mainConfig).getServers();

        doReturn(mainConfig).when(configBuilder).getConfig(configFileName);

        final Server server1 = mock(Server.class);
        final Server server2 = mock(Server.class);
        doReturn(server1).when(slowlightServer).instantiateServer(mainConfig, serverConfig1);
        doReturn(server2).when(slowlightServer).instantiateServer(mainConfig, serverConfig2);

        slowlightServer.start(new String[] {configFileName});

        verify(server1).start();
        verify(server2).start();
    }
}
