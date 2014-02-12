package com.tacitknowledge.slowlight.proxyserver.config.json;

import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.net.URL;
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
    public void configBuilderShouldLoadConfigFromConfigPath()
    {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("test-slowlight.config");
        final SlowlightConfig config = jsonConfigBuilder.getConfig(url.getPath());

        assertValidConfig(config);
    }

    @Test
    public void configBuilderShouldLoadConfigFromClasspath()
    {
        final SlowlightConfig config = jsonConfigBuilder.getConfig("test-slowlight.config");

        assertValidConfig(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void configBuilderShouldThrowExceptionIfConfigCannotBeFound()
    {
        jsonConfigBuilder.getConfig("unexistent-slowlight.config");
    }

    private void assertValidConfig(final SlowlightConfig config)
    {
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
