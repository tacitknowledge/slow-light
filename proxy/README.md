# Slow Light Proxy Server

## How to run
First, build the project with maven:

```
mvn clean install
```

Run *Slow Light Proxy Server* with

```
java -jar slowlight-proxy-<version>.jar config.xml
```

## Sample configurations

### No delays, just proxying TCP/IP.
This configuration will run server on localhost:10011 and will proxy requests to google:80

```xml
<com.tacitknowledge.slowlight.proxyserver.ServersConfiguration>
    <servers>
        <com.tacitknowledge.slowlight.proxyserver.Server>
            <port>10011</port>
            <scenarios>
                <!-- Normal scenario -->
                <com.tacitknowledge.slowlight.proxyserver.Scenario>
                    <components>
                        <com.tacitknowledge.slowlight.proxyserver.data.Proxy>
                            <remoteHost>google.com</remoteHost>
                            <remotePort>80</remotePort>
                        </com.tacitknowledge.slowlight.proxyserver.data.Proxy>
                    </components>
                </com.tacitknowledge.slowlight.proxyserver.Scenario>
            </scenarios>
        </com.tacitknowledge.slowlight.proxyserver.Server>
    </servers>
</com.tacitknowledge.slowlight.proxyserver.ServersConfiguration>
```

### Timed delays and packet discards

This configuration contains 3 scenarios:
* Simple Proxy (60% of requests)
* Proxy with timed delays for 10 sec (20% of requests)
* Discard incoming packets and keep connection open (20% of requests)

Scenario is selected proportionally to the request count in each of the scenario.

```xml
<com.tacitknowledge.slowlight.proxyserver.ServersConfiguration>
    <servers>
        <com.tacitknowledge.slowlight.proxyserver.Server>
            <port>10011</port>
            <scenarios>
                <!-- Normal scenario -->
                <com.tacitknowledge.slowlight.proxyserver.Scenario>
                    <components>
                        <com.tacitknowledge.slowlight.proxyserver.data.Proxy>
                            <remoteHost>google.com</remoteHost>
                            <remotePort>80</remotePort>
                        </com.tacitknowledge.slowlight.proxyserver.data.Proxy>
                    </components>
                    <weight>6</weight>
                </com.tacitknowledge.slowlight.proxyserver.Scenario>

                <!-- Timed delay: 10 sec before passing request to proxy delegate -->
                <com.tacitknowledge.slowlight.proxyserver.Scenario>
                    <components>
                        <com.tacitknowledge.slowlight.proxyserver.degrade.Delay>
                            <delay>10000</delay>
                            <delayOnRead>true</delayOnRead>
                        </com.tacitknowledge.slowlight.proxyserver.degrade.Delay>
                        <com.tacitknowledge.slowlight.proxyserver.data.Proxy>
                            <remoteHost>google.com</remoteHost>
                            <remotePort>80</remotePort>
                        </com.tacitknowledge.slowlight.proxyserver.data.Proxy>
                    </components>
                    <weight>2</weight>
                </com.tacitknowledge.slowlight.proxyserver.Scenario>

                <!-- Discard incoming packets and keep the connection open for up to 10 mins -->
                <com.tacitknowledge.slowlight.proxyserver.Scenario>
                    <components>
                        <com.tacitknowledge.slowlight.proxyserver.degrade.Discard>
                            <timeout>600000</timeout>
                        </com.tacitknowledge.slowlight.proxyserver.degrade.Discard>
                    </components>
                    <weight>2</weight>
                </com.tacitknowledge.slowlight.proxyserver.Scenario>
            </scenarios>
            <scenarioSelector class="com.tacitknowledge.slowlight.proxyserver.scenario.ProprotionalCountSelector"/>
        </com.tacitknowledge.slowlight.proxyserver.Server>
    </servers>
<com.tacitknowledge.slowlight.proxyserver.ServersConfiguration>
```

## Running several servers on different ports
One can run as many servers as needed on different ports. Each server may contain different set of scenarios or
different proxy delegates.
