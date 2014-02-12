package com.tacitknowledge.slowlight.embedded.config.json;

import com.google.gson.GsonBuilder;
import com.tacitknowledge.slowlight.embedded.config.MainConfig;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Configuration builder which constructs degradation configuration based on JSON config file.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class JSONConfigBuilder
{
    public static final String PROPERTY_CONFIG_FILE_NAME = "slowlight.embedded.config";
    protected static final String DEFAULT_CONFIG_FILE_NAME = "slowlight-embedded.config";

    private MainConfig config;

    /**
     * Builds and returns the configuration.
     *
     * @return configuration object.
     */
    public MainConfig getConfig()
    {
        if (config == null)
        {
            final String configJSON = getConfigJSON();

            final GsonBuilder gson = new GsonBuilder();
            config = gson.create().fromJson(configJSON, MainConfig.class);
        }

        return config;
    }

    protected String getConfigFileName()
    {
        String configFileName = System.getProperty(PROPERTY_CONFIG_FILE_NAME);
        return configFileName != null ? configFileName : DEFAULT_CONFIG_FILE_NAME;
    }

    private String getConfigJSON()
    {
        String configFileName = getConfigFileName();

        final InputStream inputStream = getConfigFileInputStream(configFileName);
        try
        {
            return new Scanner(inputStream).useDelimiter("\\Z").next();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Cannot load config file [" + configFileName + "]", e);
        }
    }

    private InputStream getConfigFileInputStream(final String configFileName)
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
    }
}
