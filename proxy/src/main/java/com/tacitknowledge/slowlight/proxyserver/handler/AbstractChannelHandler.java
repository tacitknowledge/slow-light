package com.tacitknowledge.slowlight.proxyserver.handler;

import com.tacitknowledge.slowlight.proxyserver.config.BehaviorFunctionConfig;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.behavior.BehaviorFunction;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** @author Alexandr Donciu (adonciu@tacitknowledge.com) */
@ChannelHandler.Sharable
public abstract class AbstractChannelHandler extends ChannelDuplexHandler
{
    public static final String TIME_FRAME = "timeFrame";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractChannelHandler.class);
    private static final int ZERO_TIME_FRAME = 0;

    protected AbstractConfiguration handlerParams = new MapConfiguration(new HashMap<String, Object>());

    protected HandlerConfig handlerConfig;

    protected Map<String, BehaviorFunction> behaviorFunctions = new HashMap<String, BehaviorFunction>();

    public AbstractChannelHandler(final HandlerConfig handlerConfig)
    {
        this.handlerConfig = handlerConfig;

        initBehaviorFunctions();
        initTimeFrameTask();

        registerHandlerConfig();
    }

    private void registerHandlerConfig()
    {
        populateHandlerParams();

        if (!handlerParams.isEmpty())
        {
            HandlerConfigManager.registerConfigMBean(handlerConfig, handlerParams);
        }
    }

    private void initTimeFrameTask()
    {
        new TimeFrameTask().start();
    }

    private void initBehaviorFunctions()
    {
        for (final BehaviorFunctionConfig behaviorFunctionConfig : handlerConfig.getBehaviorFunctions())
        {
            if (!handlerConfig.getParams().containsKey(behaviorFunctionConfig.getParamName()))
            {
                throw new IllegalArgumentException("Cannot map behavior function to specified handler param ["
                        + behaviorFunctionConfig.getParamName() + "] because it doesn't exists ");
            }

            behaviorFunctions.put(behaviorFunctionConfig.getType(), createBehaviorFunction(behaviorFunctionConfig.getType()));
        }
    }

    protected void populateHandlerParams()
    {
        // EMPTY
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        LOG.error("Error occurred while executing channel handler", cause);

        closeOnFlush(ctx.channel());
    }

    public void closeOnFlush(Channel channel)
    {
        if (channel != null && channel.isActive())
        {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected long getTimeFrame()
    {
        final String timeFrameProp = handlerConfig.getParam(TIME_FRAME, false);
        return timeFrameProp == null ? ZERO_TIME_FRAME : Long.parseLong(timeFrameProp);
    }

    protected void timerCallback()
    {
        // EMPTY
    }

    public HandlerConfig getHandlerConfig()
    {
        return handlerConfig;
    }

    protected void evaluateBehaviorFunctions()
    {
        for (final BehaviorFunctionConfig behaviorFunctionConfig : handlerConfig.getBehaviorFunctions())
        {
            final BehaviorFunction behaviorFunction = behaviorFunctions.get(behaviorFunctionConfig.getType());

            handlerParams.setProperty(behaviorFunctionConfig.getParamName(), behaviorFunction.evaluate(behaviorFunctionConfig.getParams()));
        }
    }

    private BehaviorFunction createBehaviorFunction(final String type)
    {
        try
        {
            final Class<?> behaviorFunctionClass = Class.forName(type);
            return (BehaviorFunction) behaviorFunctionClass.newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Cannot create behavior function by specified name [" + type + "]", e);
        }
    }

    private class TimeFrameTask implements TimerTask
    {
        private Timer timer;

        public void start()
        {
            if (getTimeFrame() > ZERO_TIME_FRAME)
            {
                if (timer == null)
                {
                    timer = new HashedWheelTimer(1, TimeUnit.SECONDS);
                }

                schedule();
            }
        }

        @Override
        public void run(Timeout timeout) throws Exception
        {
            evaluateBehaviorFunctions();
            timerCallback();

            schedule();
        }

        private void schedule()
        {
            timer.newTimeout(this, getTimeFrame(), TimeUnit.SECONDS);
        }
    }
}
