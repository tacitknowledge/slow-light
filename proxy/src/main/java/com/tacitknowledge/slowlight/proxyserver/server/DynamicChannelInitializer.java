package com.tacitknowledge.slowlight.proxyserver.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.config.ServerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.AbstractChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
public class DynamicChannelInitializer extends ChannelInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger(DynamicChannelInitializer.class);

    protected final ServerConfig serverConfig;

    private final Lock cacheLock = new ReentrantLock();
    private final Map<String, AbstractChannelHandler> cachedHandlers = new HashMap<String, AbstractChannelHandler>();

    public DynamicChannelInitializer(final ServerConfig serverConfig)
    {
        this.serverConfig = serverConfig;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception
    {
        final ChannelPipeline pipeline = ch.pipeline();

        final Map<String, AbstractChannelHandler> channelHandlers = getChannelHandlers();
        for (final String channelHandlerName : channelHandlers.keySet())
        {
            pipeline.addFirst(channelHandlerName, channelHandlers.get(channelHandlerName));
        }
    }

    public Map<String, AbstractChannelHandler> getChannelHandlers()
    {
        final Map<String, AbstractChannelHandler> channelHandler = Maps.newLinkedHashMap();

        for (HandlerConfig handlerConfig : Lists.reverse(serverConfig.getHandlers()))
        {
            try
            {
                AbstractChannelHandler handler = getChannelHandler(handlerConfig);
                channelHandler.put(handlerConfig.getName(), handler);
            }
            catch (Exception e)
            {
                LOG.error("Cannot create channel handler [{}]", handlerConfig.getName(), e);
            }
        }

        return channelHandler;
    }

    private AbstractChannelHandler getChannelHandler(final HandlerConfig handlerConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException,
            NoSuchMethodException
    {
        AbstractChannelHandler handler = null;

        cacheLock.lock();

        try
        {
            handler = cachedHandlers.get(handlerConfig.getName());

            if (handler == null)
            {
                handler = createChannelHandler(handlerConfig);

                if (handlerConfig.isReusable())
                {
                    cachedHandlers.put(handlerConfig.getName(), handler);
                }
            }
        }
        finally {
            cacheLock.unlock();
        }

        return handler;
    }

    private AbstractChannelHandler createChannelHandler(final HandlerConfig handlerConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException,
            NoSuchMethodException
    {
        final Class<?> handlerClass = Class.forName(handlerConfig.getType());
        return (AbstractChannelHandler) handlerClass.getConstructor(HandlerConfig.class).newInstance(handlerConfig);
    }
}
