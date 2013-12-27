package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a handler configuration model.
 * To configure a handler you have to specify handler name, type - fully qualified class name and parameters if any.
 * Also it is possible to configure a handler so it is reused or not between communication channels (same instance of handler),
 * this could be configured by setting <b>reusable</b> property accordingly - by default this property is set to true.<br/>
 * <br/>
 * <b>An example of handler configuration (JSON)<b/>
 *
 * <pre>
 * {@code
 * ...
 * "handlers" : [
 *      {
 *          "name" : "delayHandler",
 *          "type" : "com.tacitknowledge.slowlight.proxyserver.handler.DelayChannelHandler",
 *          "params" : {"maxDataSize" : "0", "delay" : "500"}
 *      },
 *      {
 *          "name" : "logHandler",
 *          "type" : "com.tacitknowledge.slowlight.proxyserver.handler.LogChannelHandler",
 *          "reusable" : "true"
 *      }
 * ]
 * ...}
 * </pre>
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 * */
public class HandlerConfig extends ParametrizedConfig
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
