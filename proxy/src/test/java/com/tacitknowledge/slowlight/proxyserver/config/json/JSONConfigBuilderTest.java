package com.tacitknowledge.slowlight.proxyserver.config.json;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class JSONConfigBuilderTest
{
    private static final String CONFIG_FILE_NAME = "test-slowlight.config";

    @Spy
    private final JSONConfigBuilder jsonConfigBuilder = new JSONConfigBuilder();

    @Test
    public void configBuilderShouldCreateConfigurationFromJSON() throws FileNotFoundException
    {
        final SlowlightConfig config = jsonConfigBuilder.getConfig(CONFIG_FILE_NAME);

        assertThat(config, is(notNullValue()));

        final List<ServerConfig> serverConfigs = config.getServers();
        assertThat(serverConfigs.size(), is(equalTo(2)));

        assertThat(serverConfigs.get(0).getHandlers().size(), is(1));
        assertThat(serverConfigs.get(1).getHandlers().size(), is(2));
        assertThat(serverConfigs.get(1).getHandlers().get(0).getBehaviorFunctions().size(), is(1));

        assertThat(serverConfigs.get(0).getParams().size(), is(equalTo(0)));
        assertThat(serverConfigs.get(1).getParams().size(), is(equalTo(2)));
    }
}
