package com.tacitknowledge.slowlight.proxyserver.server;

import com.google.gson.Gson;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.SlowlightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class SlowlightServer
{
    private static final Logger LOG = LoggerFactory.getLogger(SlowlightServer.class);

    public static void main(String[] args) throws FileNotFoundException
    {
        if (args.length < 1)
        {
            throw new IllegalArgumentException("Usage: " + SlowlightServer.class.getSimpleName() + " <config_file>");
        }

        final SlowlightConfig config = loadConfig(args[0]);

        for (ServerConfig serverConfig : config.getServers())
        {
            try
            {
                final Class<?> serverClass = Class.forName(config.getServerTypes().get(serverConfig.getType()));
                final Server server  = (Server) serverClass.getConstructor(ServerConfig.class).newInstance(serverConfig);
                server.start();
            }
            catch (final Exception e)
            {
                LOG.error("Cannot create server [{}]", serverConfig.getId(), e);
            }
        }
    }

    private static SlowlightConfig loadConfig(final String configFileName) throws FileNotFoundException
    {
        final File configFile = new File(configFileName);

        try
        {
            final String configJson = new Scanner(configFile).useDelimiter("\\Z").next();
            return new Gson().fromJson(configJson, SlowlightConfig.class);
        }
        catch (final FileNotFoundException e)
        {
            throw new IllegalArgumentException("Cannot find config file [" + configFileName + "]");
        }
    }
}
