package com.tacitknowledge.slowlight.proxyserver.config.json;

import com.google.gson.Gson;
import com.tacitknowledge.slowlight.proxyserver.config.ConfigBuilder;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class JSONConfigBuilder implements ConfigBuilder
{
    @Override
    public SlowlightConfig getConfig(final String configFileName)
    {
        final File configFile = new File(configFileName);

        try
        {
            final String configJson = new Scanner(configFile).useDelimiter("\\Z").next();
            return new Gson().fromJson(configJson, SlowlightConfig.class);
        }
        catch (final FileNotFoundException e)
        {
            throw new IllegalArgumentException("Cannot find config file [" + configFileName + "]");
        }
    }
}
