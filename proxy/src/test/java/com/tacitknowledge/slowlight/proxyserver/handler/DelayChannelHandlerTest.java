package com.tacitknowledge.slowlight.proxyserver.handler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.MapConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import com.tacitknowledge.slowlight.proxyserver.config.BehaviorFunctionConfig;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import com.tacitknowledge.slowlight.proxyserver.handler.behavior.BehaviorFunction;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DelayChannelHandlerTest extends BaseChannelHandlerTest
{
	@Test
	public void handlerShouldSplitDataIntoFragmentsAndDelayResponse()
	        throws Exception {
		final String messageContent = "message data to be sent";

		final int dataSize = 3;
		final long delay = 100;

		doReturn(Integer.toString(dataSize)).when(handlerConfig).getParam(
		        DelayChannelHandler.PARAM_MAX_DATA_SIZE);
		doReturn(Long.toString(delay)).when(handlerConfig).getParam(
		        DelayChannelHandler.PARAM_DELAY);

		final ByteBuf message = Unpooled.wrappedBuffer(messageContent
		        .getBytes());

		final DelayChannelHandler delayChannelHandler = new DelayChannelHandler(
		        handlerConfig);

		delayChannelHandler.write(channelHandlerContext, message, promise);

		verify(eventExecutor).schedule((Runnable) Matchers.anyObject(),
		        Matchers.eq(delay), Matchers.eq(TimeUnit.MILLISECONDS));
	}

	@Test
	public void evaluateBehaviorFunctionsTest() throws SecurityException,
	        NoSuchFieldException, IllegalArgumentException,
	        IllegalAccessException {

		List<BehaviorFunctionConfig> behaviorFunctions = new ArrayList<BehaviorFunctionConfig>();

		BehaviorFunctionConfig firstFunctionConfig = new BehaviorFunctionConfig();
		firstFunctionConfig.setParamName("name1");
		firstFunctionConfig
		        .setType("com.tacitknowledge.slowlight.proxyserver.handler.TestFunction1");
		firstFunctionConfig.setStart("1");
		firstFunctionConfig.setStop("10000");
		behaviorFunctions.add(firstFunctionConfig);

		BehaviorFunctionConfig secondFunctionConfig = new BehaviorFunctionConfig();
		secondFunctionConfig.setParamName("name1");
		secondFunctionConfig
		        .setType("com.tacitknowledge.slowlight.proxyserver.handler.TestFunction2");
		secondFunctionConfig.setStart("10000");
		behaviorFunctions.add(secondFunctionConfig);

		Map params = new HashMap();
		params.put("name1", 34);
		params.put(DelayChannelHandler.PARAM_DELAY, "1");
		params.put(DelayChannelHandler.PARAM_MAX_DATA_SIZE, "2");

		HandlerConfig handlerConfig1 = new HandlerConfig();

		handlerConfig1.setParams(params);
		handlerConfig1.setBehaviorFunctions(behaviorFunctions);

		final DelayChannelHandler delayChannelHandler = new DelayChannelHandler(
		        handlerConfig1);

		delayChannelHandler.evaluateBehaviorFunctions();

		Field field = delayChannelHandler.getClass().getSuperclass()
		        .getDeclaredField("handlerParams");
		field.setAccessible(true);
		MapConfiguration handlerParamsMap = (MapConfiguration) field
		        .get(delayChannelHandler);
		Assert.assertEquals(12, handlerParamsMap.getInt("name1"));

		try {
			synchronized (Thread.currentThread()) {
				Thread.currentThread().wait(11000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		delayChannelHandler.evaluateBehaviorFunctions();

		handlerParamsMap = (MapConfiguration) field
		        .get(delayChannelHandler);
		Assert.assertEquals(24, handlerParamsMap.getInt("name1"));
	}
}

class TestFunction1 extends BehaviorFunction {

	@Override
	public Object evaluate(Map<String, ?> params) {

		return 12;
	}
}

class TestFunction2 extends BehaviorFunction {

	@Override
	public Object evaluate(Map<String, ?> params) {

		return 24;
	}
}
