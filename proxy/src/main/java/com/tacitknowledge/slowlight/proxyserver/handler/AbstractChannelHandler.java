package com.tacitknowledge.slowlight.proxyserver.handler;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.slowlight.proxyserver.config.BehaviorFunctionConfig;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.behavior.BehaviorFunction;

/**
 * Implementation of slow-light abstract handler.
 * Use this abstract handler as extension point for all existing and future handlers.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@ChannelHandler.Sharable
public abstract class AbstractChannelHandler extends ChannelDuplexHandler
{
    public static final String TIME_FRAME = "timeFrame";
    public static final int ZERO_TIME_FRAME = 0;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractChannelHandler.class);

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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        LOG.error("Error occurred while executing channel handler", cause);

        closeOnFlush(ctx.channel());
    }

    /**
     * Flush all pending messages and then close the channel.
     *
     * @param channel the channel to be actioned
     */
    public void closeOnFlush(Channel channel)
    {
        if (channel != null && channel.isActive())
        {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Gets the current handler time frame configuration.
     *
     * @return time frame configuration (in seconds)
     */
    protected long getTimeFrame()
    {
        final String timeFrameProp = handlerConfig.getParam(TIME_FRAME, false);
        return timeFrameProp == null ? ZERO_TIME_FRAME : Long.parseLong(timeFrameProp);
    }

    /**
     * This callback method gets invoked per each time frame.
     * Use it to do any handler time dependent changes (e.g. update handler parameters, expose metrics, etc.)
     */
    protected void timerCallback()
    {
        // EMPTY
    }

    /**
     * Override this method in a concrete handler to lookup, transform and populate all required parameters.
     */
    protected void populateHandlerParams()
    {
        // EMPTY
    }

    /**
     * Cycles through all defined behaviour function {@link com.tacitknowledge.slowlight.proxyserver.handler.behavior.IntervalBehaviorFunction}
     * and evaluates handler parameters based on function result.
     */
    protected void evaluateBehaviorFunctions()
    {
        for (final BehaviorFunctionConfig behaviorFunctionConfig : handlerConfig.getBehaviorFunctions())
        {
			final BehaviorFunction behaviorFunction = behaviorFunctions.get(behaviorFunctionConfig.getId());

			if (behaviorFunction.shouldEvaluate(behaviorFunctionConfig)) {
				handlerParams.setProperty(
				        behaviorFunctionConfig.getParamName(), behaviorFunction
				                .evaluate(behaviorFunctionConfig.getParams()));
			}
        }
    }

    private void initTimeFrameTask()
    {
        new TimeFrameTask().start();
    }

    private void registerHandlerConfig()
    {
        populateHandlerParams();

        if (!handlerParams.isEmpty())
        {
            HandlerConfigManager.registerConfigMBean(handlerConfig, handlerParams);
        }
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

			behaviorFunctions.put(behaviorFunctionConfig.getId(), createBehaviorFunction(behaviorFunctionConfig));
        }
    }

    private BehaviorFunction createBehaviorFunction(final BehaviorFunctionConfig config)
    {
        try
        {
            final Class<?> behaviorFunctionClass = Class.forName(config.getType());
            return (BehaviorFunction) behaviorFunctionClass.getConstructor(BehaviorFunctionConfig.class).newInstance(config);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Cannot create behavior function by specified name [" + config.getType() + "]", e);
        }
    }

    public HandlerConfig getHandlerConfig()
    {
        return handlerConfig;
    }

    /**
     * This class drives the handler time frame functionality.
     */
    protected class TimeFrameTask implements TimerTask
    {
        protected Timer timer;

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

        protected void schedule()
        {
            timer.newTimeout(this, getTimeFrame(), TimeUnit.SECONDS);
        }
    }
}
