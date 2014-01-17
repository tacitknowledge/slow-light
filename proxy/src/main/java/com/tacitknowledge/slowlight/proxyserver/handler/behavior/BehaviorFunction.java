package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tacitknowledge.slowlight.proxyserver.config.BehaviorFunctionConfig;

/**
 * Determines an output parameter value based on input function parameters.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public abstract class BehaviorFunction
{
	private final long initTime = System.currentTimeMillis();

	private Long start;

	private Long stop;

	/**
	 * Evaluates a function using specified parameters.
	 *
	 * @param params
	 *            function parameters
	 * @return evaluation result
	 */
	public abstract Object evaluate(final Map<String, ?> params);

	public void preEvaluateInit(BehaviorFunctionConfig config) {
		setStart(config.getStart());
		setStop(config.getStop());
	}

	public BehaviorFunction setStart(String start) {
		if (!StringUtils.isEmpty(start)) {
			this.start = Long.parseLong(start);
		} else {
			this.start = null;
		}
		return this;
	}

	public BehaviorFunction setStop(String stop) {
		if (!StringUtils.isEmpty(stop)) {
			this.stop = Long.parseLong(stop);
		} else {
			this.stop = null;
		}

		return this;
	}

	public boolean shouldEvaluate() {

		if (start == null && stop == null) {
			return true;
		}

		long delta = System.currentTimeMillis() - initTime;

		if (start != null && stop == null && delta >= start) {
			return true;
		}

		if (start == null && stop != null && delta < stop) {
			return true;
		}

		if (start != null && stop != null && delta >= start && delta < stop) {
			return true;
		}

		return false;

	}
}
