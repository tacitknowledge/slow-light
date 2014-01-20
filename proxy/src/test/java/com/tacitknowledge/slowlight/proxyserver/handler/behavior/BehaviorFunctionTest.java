package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.slowlight.proxyserver.config.BehaviorFunctionConfig;

public class BehaviorFunctionTest {

	private static final String PARAM_NAME = "paramName";

	private static final String TYPE = "com.tacitknowledge.slowlight.proxyserver.handler.behavior.SinusoidalBehavior";

	IntervalBehaviorFunction function;

	BehaviorFunctionConfig config;

	@Before
	public void before() {
		config = new BehaviorFunctionConfig();
		function = spy(new IntervalBehaviorFunction() {
			@Override
			public Object evaluate(Map<String, ?> params) {
				return null;
			}
		});
	}

	public void wait(int milliseconds) {
		synchronized (Thread.currentThread()) {
			try {
				Thread.currentThread().wait(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	@Test
	public void shouldEvaluateWithCorrectStartTest() {

		BehaviorFunctionConfig config = new BehaviorFunctionConfig();
		Map<String, String> ranges = new HashMap<String, String>();
		config.setRanges(new HashMap<String, String>());
		config.getRanges().put("1", null);

		wait(5);

		Assert.assertTrue(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithWrongStartTest() {
		IntervalBehaviorFunction function = spy(new IntervalBehaviorFunction() {
			@Override
			public Object evaluate(Map<String, ?> params) {
				return null;
			}
		});

		BehaviorFunctionConfig config = new BehaviorFunctionConfig();
		config.getRanges().put("10000", "");

		Assert.assertFalse(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithWrongEndTest() {
		BehaviorFunctionConfig config = new BehaviorFunctionConfig();
		config.setParams(new HashMap<String, String>());
		config.getRanges().put("", "2");

		wait(5);

		Assert.assertFalse(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithCorrectEndTest() {
		config.setParams(new HashMap<String, String>());
		config.getRanges().put("", "20000");

		Assert.assertTrue(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithCorrectIntervalTest() {
		config.setParams(new HashMap<String, String>());
		config.getRanges().put("1000", "5000");

		wait(2000);

		Assert.assertTrue(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithWrongIntervalTest() {
		config.setParams(new HashMap<String, String>());
		config.getRanges().put("1000", "5000");

		wait(5000);

		Assert.assertFalse(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithNoIntervalTest() {
		Assert.assertTrue(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithRangesTest() {
		config.setParams(new HashMap<String, String>());

		Map<String, String> ranges = new HashMap<String, String>();
		ranges.put("0", "50000");
		config.setRanges(ranges);
		Assert.assertTrue(function.shouldEvaluate(config));
	}

	@Test
	public void shouldEvaluateWithWrongRangesTest() {
		config.setParams(new HashMap<String, String>());

		Map<String, String> ranges = new HashMap<String, String>();
		ranges.put("50000", "150000");

		config.setRanges(ranges);

		Assert.assertFalse(function.shouldEvaluate(config));
	}

	@Test
	public void getIdTest() {
		config.setParamName(PARAM_NAME);
		config.setType(TYPE);
		config.getRanges().put("12345", "12345678");
		Assert.assertEquals(PARAM_NAME + "_" + TYPE + "[12345" + " - " + "12345678]", config.getId());
	}
}
