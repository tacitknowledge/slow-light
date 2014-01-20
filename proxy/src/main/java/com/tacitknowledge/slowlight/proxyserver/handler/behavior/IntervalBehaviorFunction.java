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
	/**
	 * Time at which was initiated this function.
	 */
	private final long initTime = System.currentTimeMillis();

	/**
	 * Convert a string value into a long value. This is an utility function
	 * that should be moved into an other class
	 *
	 * @param value
	 *            that should be parsed into a long value
	 * @return
	 */
	public Long getMilliseconds(String value)
    {
		if (!StringUtils.isEmpty(value))
        {
			return Long.parseLong(value);
		}
        else
        {
			return null;
		}
	}

	/**
	 * Test if this behavior function should be evaluated for a given time
	 * range.
	 *
	 * @return verification result.
	 */
	protected boolean shouldEvaluate(Long start, Long stop)
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

	/**
	 * Check if this behavior function should be evaluated at a certain time.
	 * Consider all time ranges.
	 *
	 * @return verification result
	 */
	@Override
	public boolean shouldEvaluate(BehaviorFunctionConfig functionConfig)
	{
		if (functionConfig.getRanges() == null
		        || functionConfig.getRanges().isEmpty()) {
			return true;
		}

		Iterator<String> keys = functionConfig.getRanges().keySet().iterator();
		while (keys.hasNext()) {
			String start = keys.next();
			String stop = functionConfig.getRanges().get(start);
			if (shouldEvaluate(getMilliseconds(start), getMilliseconds(stop))) {
				return true;
			}
		}
		return false;
	}
}
