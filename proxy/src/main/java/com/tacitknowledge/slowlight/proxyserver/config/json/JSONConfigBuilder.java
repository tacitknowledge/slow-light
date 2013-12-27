package com.tacitknowledge.slowlight.proxyserver.config.json;

import com.google.gson.GsonBuilder;
import com.tacitknowledge.slowlight.proxyserver.config.ConfigBuilder;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

/**
 * Config builder implementation to create slowlight configuration from an JSON based configuration file.
 * Config builder will initially try to load the JSON configuration file from given path and then from classpath.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class JSONConfigBuilder implements ConfigBuilder
{
    private static final Logger LOG = LoggerFactory.getLogger(JSONConfigBuilder.class);

    @Override
    public SlowlightConfig getConfig(final String configFileName)
    {
        final String configJson = getConfigJSON(configFileName);

        final GsonBuilder gson = new GsonBuilder();
        return gson.create().fromJson(configJson, SlowlightConfig.class);
    }

    private String getConfigJSON(final String configFileName)
    {
        LOG.info("Load slowlight configuration [{}]", configFileName);

        File configFile = getConfigFromPath(configFileName);

        if (configFile == null)
        {
            configFile = getConfigFromClasspath(configFileName);
        }

        try
        {
            return new Scanner(configFile).useDelimiter("\\Z").next();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Cannot find config file [" + configFileName + "]", e);
        }
    }

    private File getConfigFromPath(final String configFileName)
    {
        LOG.debug("Loading configuration file [{}] from the path", configFileName);

        File configFile = new File(configFileName);
        if (!configFile.exists())
        {
            configFile = null;
        }

        return configFile;
    }

    private File getConfigFromClasspath(final String configFileName)
    {
        LOG.debug("Loading configuration file [{}] from the classpath", configFileName);

        File configFile = null;

        final URL url = Thread.currentThread().getContextClassLoader().getResource(configFileName);
        if (url != null)
        {
            configFile = new File(url.getFile());
        }

        return configFile;
    }
}
