package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ParameterizedConfig
{
    private Map<String, String> params = new HashMap<String, String>();

    public void setParams(final Map<String, String> params)
    {
        this.params = params;
    }

    public Map<String, String> getParams()
    {
        return params;
    }

    public String getParam(final String key)
    {
        return getParam(key, true);
    }

    public String getParam(final String key, final boolean required)
    {
        final String param = params.get(key);

        if (required && param == null)
        {
            throw new IllegalArgumentException("Config parameter [" + key + "] doesn't exists");
        }

        return param;
    }
}
