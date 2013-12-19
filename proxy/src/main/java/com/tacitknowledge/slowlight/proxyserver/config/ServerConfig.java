package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.ArrayList;
import java.util.List;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class ServerConfig extends ParameterizedConfig
{
    private String id;
    private String type;
    private int localPort;

    private List<HandlerConfig> handlers = new ArrayList<HandlerConfig>();

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public int getLocalPort()
    {
        return localPort;
    }

    public void setLocalPort(final int localPort)
    {
        this.localPort = localPort;
    }

    public List<HandlerConfig> getHandlers()
    {
        return handlers;
    }

    public void setHandlers(final List<HandlerConfig> handlers)
    {
        this.handlers = handlers;
    }
}
