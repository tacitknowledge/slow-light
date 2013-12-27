package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a server configuration model.
 * To configure a server you have to specify the id of the server, type - type alias (this must be specified in the serverTypes
 * list see {@link com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig}), local port this server will listen on,
 * handlers stack (see {@link com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig}) and server params if any.<br/>
 *
 * <br/>
 * <b>An example of servers configuration (JSON)<b/>
 * <pre>
 * {@code
 * "servers" : [
 *      {
 *          "id" : "testServer1",
 *          "type" : "simple",
 *          "localPort" : "9011",
 *          "handlers" : [
 *              ...
 *          ]
 *      },
 *      {
 *          "id" : "testServer2",
 *          "type" : "proxy",
 *          "localPort" : "9012",
 *          "params" : {
 *              "host" : "localhost",
 *              "port" : "8080"
 *          },
 *          "handlers" : [
 *              ...
 *          ]
 *      }
 * ]
 * }
 * </pre>
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 * */
public class ServerConfig extends ParametrizedConfig
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
