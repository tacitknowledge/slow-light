package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a slowlight server configuration model.
 * To configure slowlight server you have to specify <b>serverTypes</b> (a mapping between server type name (alias) and
 * fully qualified class name which provides the server implementation) and <b>servers</b> that follow to be configured and
 * started by slowlight (for more details see {@link com.tacitknowledge.slowlight.proxyserver.config.ServerConfig}).
 *
 * <br/>
 * <b>An example of slowlight server configuration (JSON)<b/>
 * <pre>
 * {@code
 * {
 *      serverTypes : {
 *          "simple" : "com.tacitknowledge.slowlight.proxyserver.server.simple.SimpleServer",
 *          "proxy" : "com.tacitknowledge.slowlight.proxyserver.server.proxy.ProxyServer"
 *      },
 *      "servers" : [
 *          ...
 *      ]
 * }
 * }
 * </pre>
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 * */
public class SlowlightConfig extends ParametrizedConfig
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
