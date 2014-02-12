package com.tacitknowledge.slowlight.embedded;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DegradationHandlerTest {

	class A {
		public void test(Integer arg) {
		}
	}

	@Test
	public void isMethodExcludedTest() throws SecurityException,
	        NoSuchMethodException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 500l,
		        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		DegradationStrategy degradationStrategy = mock(DegradationStrategy.class);

		DegradationHandler degradationHandler = new DegradationHandler(
		        executor, degradationStrategy);

		Method method = A.class.getMethod("test",
		        new Class[] { Integer.class });

		doReturn(true).when(degradationStrategy).isMethodExcluded(method);

		assertTrue(degradationHandler.isMethodExcluded(method));
	}

	@Test
	public void callDirectlyTest() throws Exception {
		TargetCallback targetCallback = mock(TargetCallback.class);

		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 500l,
		        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		DegradationStrategy degradationStrategy = mock(DegradationStrategy.class);

		DegradationHandler degradationHandler = new DegradationHandler(
		        executor, degradationStrategy);

		degradationHandler.callDirectly(targetCallback);

		verify(targetCallback, times(1)).execute();
	}
}
