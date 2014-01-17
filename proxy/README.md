# Slow Light Proxy Server

## How to run
First, build the project with maven:

```
mvn clean install
```

Run *Slow Light Proxy Server* with

```
java -jar slowlight-proxy-<version>.jar config.json
```

## Sample configurations

### No delays, just proxying TCP/IP.
This configuration will run server on localhost:10011 and will proxy requests to google:80

```json
{
    "serverTypes" : {
        "simple" : "com.tacitknowledge.slowlight.proxyserver.server.simple.SimpleServer",
        "proxy" : "com.tacitknowledge.slowlight.proxyserver.server.proxy.ProxyServer"
    },

    "servers" : [
        {
            "id" : "proxyServerExample",
            "type" : "proxy",
            "localPort" : "10011",
            "params" : {
                "host" : "google.com",
                "port" : "80"
            }
        }
    ]
}
```

### Timed delays and packet discards

This configuration contains 3 scenarios:
* Simple Proxy (first 2 min)
* Proxy with timed delays for 10 sec (after 2 min)
* Discard incoming packets and keep connection open (after 5 min)

```json
{
    "id" : "solrServer",
    "type" : "proxy",
    "localPort" : "9012",
    "params" : {
        "host" : "localhost",
        "port" : "8983"
    },
    "handlers" : [
        {
            "name" : "delayHandler",
            "type" : "com.tacitknowledge.slowlight.proxyserver.handler.DelayChannelHandler",
            "params" : {"maxDataSize" : "0", "delay" : "0", "timeFrame" : "5"},
            "behaviorFunctions" : [
                {
                    "paramName" : "delay",
                    "type" : "com.tacitknowledge.slowlight.proxyserver.handler.behavior.LinearBehavior",
                    "start" : "120000",
                    "params" : {
                        "value" : "500"
                    }
                }
            ]
        },
        {
            "name" : "discardHandler",
            "type" : "com.tacitknowledge.slowlight.proxyserver.handler.DiscardChannelHandler",
            "params" : {"enabled" : "false", "timeFrame" : "5"},
            "behaviorFunctions" : [
                {
                    "paramName" : "enabled",
                    "type" : "com.tacitknowledge.slowlight.proxyserver.handler.behavior.LinearBehavior",
                    "start" : "300000",
                    "params" : {
                        "value" : "true"
                    }
                }
            ]
        }
    ]
}
```

## Available handlers

* com.tacitknowledge.slowlight.proxyserver.handler.CloseConnectionChannelHandler - closes connection after a given time
* com.tacitknowledge.slowlight.proxyserver.handler.DelayChannelHandler - delays response data by specified time
* com.tacitknowledge.slowlight.proxyserver.handler.DiscardChannelHandler - discards request data and keeps the connection open
* com.tacitknowledge.slowlight.proxyserver.handler.RandomDataChannelHandler - generates random data by specified parameters
* com.tacitknowledge.slowlight.proxyserver.handler.LogChannelHandler - logs some basic information about request/response messages

## configuration notes
Slow Light Proxy Server uses Gson to load the ServersConfiguration object graph from the specified configuration file.

## Running several servers on different ports
One can run as many servers as needed on different ports. Each server may contain different set of handlers and
different proxy delegates.
