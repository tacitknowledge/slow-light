package com.tacitknowledge.slowlight.proxyserver.config;

/**
 * Slow-light configuration exception. This exception must be thrown whenever a configuration issue occurs.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ConfigException extends Exception
{
    public ConfigException(final String message)
    {
        super(message);
    }

    public ConfigException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
