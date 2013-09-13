package com.tacitknowledge.slowlight.proxyserver;

import java.util.List;

import com.google.common.collect.Lists;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ServersConfiguration
{
    private final List<Server> servers;

    public ServersConfiguration() {
        servers = Lists.newArrayList();
    }

    public ServersConfiguration(final List<Server> servers)
    {
        this.servers = servers;
    }

    public List<Server> getServers()
    {
        return servers;
    }
}
