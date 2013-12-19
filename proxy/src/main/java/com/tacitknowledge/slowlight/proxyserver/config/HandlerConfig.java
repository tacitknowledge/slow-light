package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.ArrayList;
import java.util.List;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class HandlerConfig extends ParameterizedConfig
{
    public static final HandlerConfig EMPTY = new HandlerConfig();

    private String name;
    private String type;
    private boolean reusable = true;

    private List<BehaviorFunctionConfig> behaviorFunctions = new ArrayList<BehaviorFunctionConfig>();

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public boolean isReusable()
    {
        return reusable;
    }

    public void setReusable(final boolean reusable)
    {
        this.reusable = reusable;
    }

    public List<BehaviorFunctionConfig> getBehaviorFunctions()
    {
        return behaviorFunctions;
    }

    public void setBehaviorFunctions(final List<BehaviorFunctionConfig> behaviorFunctions)
    {
        this.behaviorFunctions = behaviorFunctions;
    }
}
