package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class SlowlightConfig extends ParameterizedConfig
{
    private Map<String, String> serverTypes;

    private List<ServerConfig> servers = new ArrayList<ServerConfig>();

    public Map<String, String> getServerTypes()
    {
        return serverTypes;
    }

    public void setServerTypes(final Map<String, String> serverTypes)
    {
        this.serverTypes = serverTypes;
    }

    public List<ServerConfig> getServers()
    {
        return servers;
    }

    public void setServers(final List<ServerConfig> servers)
    {
        this.servers = servers;
    }
}
