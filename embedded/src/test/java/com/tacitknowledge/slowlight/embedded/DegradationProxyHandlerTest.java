package com.tacitknowledge.slowlight.embedded;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

public class DegradationProxyHandlerTest {



	class A {
		public void test(Integer argument) {
		}
	}

	@Test
	public void testDirectMethodInvocation() throws NumberFormatException,
	        Throwable {

		DegradationHandler degradationHandler = mock(DegradationHandler.class);
		A target = new A();

		DegradationProxyHandler proxy = new DegradationProxyHandler(target,
		        degradationHandler);

		Method method = A.class.getMethod("test", Integer.class);

		doReturn(true).when(degradationHandler).isMethodExcluded(method);

		doReturn(1).when(degradationHandler).callDirectly(
		        Mockito.any(TargetCallback.class));

		Assert.assertEquals(
		        1,
		        proxy.invoke(target, method, method,
		                new Object[] { Integer.parseInt("1") }));
		;
	}

	@Test
	public void testIndirectMethodInvocation() throws NumberFormatException,
	        Throwable {

		DegradationHandler degradationHandler = mock(DegradationHandler.class);
		A target = new A();

		DegradationProxyHandler proxy = new DegradationProxyHandler(target,
		        degradationHandler);

		Method method = A.class.getMethod("test", Integer.class);

		doReturn(false).when(degradationHandler).isMethodExcluded(method);

		doReturn(2).when(degradationHandler).invoke(
		        Mockito.any(TargetCallback.class));

		Assert.assertEquals(
		        2,
		        proxy.invoke(target, method, method,
		                new Object[] { Integer.parseInt("1") }));
		;
	}
}
