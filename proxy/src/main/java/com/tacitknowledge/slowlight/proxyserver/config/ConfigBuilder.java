package com.tacitknowledge.slowlight.proxyserver.config;

/**
 * Interface definition for any objects capable to build a slow-light configuration.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public interface ConfigBuilder
{
    /**
     * Returns the slow-light configuration.
     *
     * @param configFileName  name of the file containing config information
     * @return  configuration object
     * @throws ConfigException if configuration cannot be obtained or invalid
     */
    SlowlightConfig getConfig(final String configFileName) throws ConfigException;
}
