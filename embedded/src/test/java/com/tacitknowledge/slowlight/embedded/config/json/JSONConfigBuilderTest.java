package com.tacitknowledge.slowlight.embedded.config.json;

import com.tacitknowledge.slowlight.embedded.config.MainConfig;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class JSONConfigBuilderTest
{
    private JSONConfigBuilder jsonConfigBuilder;

    @Before
    public void setup()
    {
        System.clearProperty(JSONConfigBuilder.PROPERTY_CONFIG_FILE_NAME);

        jsonConfigBuilder = new JSONConfigBuilder();
    }

    @Test
    public void configBuilderShouldLoadConfigFileBySpecifiedSystemProperty()
    {
        System.setProperty(JSONConfigBuilder.PROPERTY_CONFIG_FILE_NAME, "test-slowlight-embedded.config");

        final MainConfig mainConfig = jsonConfigBuilder.getConfig();

        assertThat(mainConfig, is(notNullValue()));
        assertThat(mainConfig.getRules().size(), is(equalTo(2)));

        assertThat(mainConfig.getRules().get(0).getServiceDemandTime(), is(notNullValue()));
        assertThat(mainConfig.getRules().get(0).getServiceTimeout(), is(notNullValue()));
        assertThat(mainConfig.getRules().get(0).getPassRate(), is(notNullValue()));
        assertThat(mainConfig.getRules().get(0).getThreads(), is(notNullValue()));
        assertThat(mainConfig.getRules().get(0).getApplyTo().size(), is(equalTo(2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void configBuilderShouldThrowExceptionIfCannotLoadConfigFile()
    {
        System.setProperty(JSONConfigBuilder.PROPERTY_CONFIG_FILE_NAME, "non-existent-config");

        jsonConfigBuilder.getConfig();
    }

    @Test
    public void configBuilderShouldReturnDefaultConfigFileNameIfNoSystemPropertySpecified()
    {
        final String configFileName = jsonConfigBuilder.getConfigFileName();

        assertThat(configFileName, is(equalTo(JSONConfigBuilder.DEFAULT_CONFIG_FILE_NAME)));
    }
}
