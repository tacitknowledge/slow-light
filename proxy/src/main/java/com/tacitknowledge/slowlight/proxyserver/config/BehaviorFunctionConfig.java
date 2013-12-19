package com.tacitknowledge.slowlight.proxyserver.config;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class BehaviorFunctionConfig extends ParameterizedConfig
{
    private String paramName;
    private String type;

    public String getParamName()
    {
        return paramName;
    }

    public void setParamName(final String paramName)
    {
        this.paramName = paramName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }
}
