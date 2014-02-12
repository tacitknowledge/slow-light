package com.tacitknowledge.slowlight.proxyserver.server;

import com.tacitknowledge.slowlight.proxyserver.config.ConfigBuilder;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;
import com.tacitknowledge.slowlight.proxyserver.config.json.JSONConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Slowlight server main class. Use this class to start an instance of slow-light server.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class SlowlightServer
{
    private static final Logger LOG = LoggerFactory.getLogger(SlowlightServer.class);

    private ConfigBuilder configBuilder;

    public static void main(String[] args) throws Exception
    {
        final SlowlightServer slowlightServer = new SlowlightServer();
        slowlightServer.setConfigBuilder(new JSONConfigBuilder());

        slowlightServer.start(args);
    }

    public void start(final String[] args) throws Exception
    {
        validateArguments(args);

        final SlowlightConfig config = configBuilder.getConfig(args[0]);
        for (ServerConfig serverConfig : config.getServers())
        {
            try
            {
                final Server server = instantiateServer(config, serverConfig);
                server.start();
            }
            catch (final Exception e)
            {
                LOG.error("Cannot create server [{}]", serverConfig.getId(), e);
                throw e;
            }
        }
    }

    protected Server instantiateServer(final SlowlightConfig config, final ServerConfig serverConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final Class<?> serverClass = Class.forName(config.getServerTypes().get(serverConfig.getType()));
        return (Server) serverClass.getConstructor(ServerConfig.class).newInstance(serverConfig);
    }

    private void validateArguments(final String[] args)
    {
        if (args.length < 1)
        {
            throw new IllegalArgumentException("Usage: " + SlowlightServer.class.getSimpleName() + " <config_file>");
        }
    }

    public void setConfigBuilder(final ConfigBuilder configBuilder)
    {
        this.configBuilder = configBuilder;
    }
}
