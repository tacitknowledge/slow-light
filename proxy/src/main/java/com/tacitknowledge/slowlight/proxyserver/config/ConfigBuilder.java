package com.tacitknowledge.slowlight.proxyserver.config;

/**
 * Interface definition for any objects capable to build a slow-light configuration.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public interface ConfigBuilder
{
    /**
     * Creates and returns the slow-light configuration based on the specified config file.
     *
     * @param configFileName  name of the file containing config information
     * @return  configuration object
     * @throws ConfigException if configuration cannot be obtained or invalid
     */
    SlowlightConfig getConfig(final String configFileName) throws ConfigException;
}
