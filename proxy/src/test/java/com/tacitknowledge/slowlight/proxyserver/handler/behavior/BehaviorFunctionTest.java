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
		function = spy(new IntervalBehaviorFunction(config) {
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
		config.setStart("1");
		config.setParams(new HashMap<String, String>());

		wait(5);

		function.init(config);
		Assert.assertTrue(function.shouldEvaluate());
	}

	@Test
	public void shouldEvaluateWithWrongStartTest() {
		IntervalBehaviorFunction function = spy(new IntervalBehaviorFunction(config) {
			@Override
			public Object evaluate(Map<String, ?> params) {
				return null;
			}
		});

		BehaviorFunctionConfig config = new BehaviorFunctionConfig();
		config.setParams(new HashMap<String, String>());
		config.setStart("10000");

		function.init(config);
		Assert.assertFalse(function.shouldEvaluate());
	}

	@Test
	public void shouldEvaluateWithWrongEndTest() {
		BehaviorFunctionConfig config = new BehaviorFunctionConfig();
		config.setParams(new HashMap<String, String>());
		config.setStop("2");

		wait(5);

		function.init(config);
		Assert.assertFalse(function.shouldEvaluate());
	}

	@Test
	public void shouldEvaluateWithCorrectEndTest() {
		config.setParams(new HashMap<String, String>());
		config.setStop("20000");

		function.init(config);
		Assert.assertTrue(function.shouldEvaluate());
	}

	@Test
	public void shouldEvaluateWithCorrectIntervalTest() {
		config.setParams(new HashMap<String, String>());
		config.setStop("5000");
		config.setStart("1000");

		wait(2000);

		function.init(config);
		Assert.assertTrue(function.shouldEvaluate());
	}

	@Test
	public void shouldEvaluateWithWrongIntervalTest() {
		config.setParams(new HashMap<String, String>());
		config.setStop("5000");
		config.setStart("1000");

		wait(5000);

		function.init(config);
		Assert.assertFalse(function.shouldEvaluate());
	}

	@Test
	public void shouldEvaluateWithNoIntervalTest() {
		config.setParams(new HashMap<String, String>());
		function.init(config);
		Assert.assertTrue(function.shouldEvaluate());
	}

	@Test
	public void getIdTest() {
		config.setParamName(PARAM_NAME);
		config.setType(TYPE);
		config.setStart("12345");
		config.setStop("12345678");
		Assert.assertEquals(PARAM_NAME + "_" + TYPE + "[12345" + " - " + "12345678]", config.getId());
	}
}
