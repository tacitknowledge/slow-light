package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.tacitknowledge.slowlight.proxyserver.config.BehaviorFunctionConfig;

/**
 * Determines an output parameter value based on input function parameters.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public abstract class IntervalBehaviorFunction implements BehaviorFunction
{
	private final long initTime = System.currentTimeMillis();

	private Long start;
	private Long stop;

    public IntervalBehaviorFunction(final BehaviorFunctionConfig config)
    {
        init(config);
    }

    /**
     * Initialize behavior function.
     *
     * @param config behavior function configuration
     */
	public void init(BehaviorFunctionConfig config)
    {
		setStart(config.getStart());
		setStop(config.getStop());
	}

    public IntervalBehaviorFunction setStart(String start)
    {
		if (!StringUtils.isEmpty(start))
        {
			this.start = Long.parseLong(start);
		}
        else
        {
			this.start = null;
		}

		return this;
	}

    public IntervalBehaviorFunction setStop(String stop)
    {
		if (!StringUtils.isEmpty(stop))
        {
			this.stop = Long.parseLong(stop);
		}
        else
        {
			this.stop = null;
		}

		return this;
	}

	protected boolean shouldEvaluate()
    {

		if (start == null && stop == null)
        {
			return true;
		}

		long delta = System.currentTimeMillis() - initTime;

		if (start != null && stop == null && delta >= start)
        {
			return true;
		}

		if (start == null && stop != null && delta < stop)
        {
			return true;
		}

		if (start != null && stop != null && delta >= start && delta < stop)
        {
			return true;
		}

		return false;
	}

	@Override
	public boolean shouldEvaluate(BehaviorFunctionConfig functionConfig)
	{
		Iterator<String> keys = functionConfig.getRanges().keySet().iterator();
		while (keys.hasNext()) {
			String start = keys.next();
			String stop = functionConfig.getRanges().get(start);
			preEvaluateInit(start, stop);
			if (shouldEvaluate()) {
				return true;
			}
		}
		return false;
	}

	public void preEvaluateInit(BehaviorFunctionConfig config) {
		preEvaluateInit(config.getStart(), config.getStop());
	}

	protected void preEvaluateInit(String start, String stop) {
		setStart(start);
		setStop(stop);
	}
}
