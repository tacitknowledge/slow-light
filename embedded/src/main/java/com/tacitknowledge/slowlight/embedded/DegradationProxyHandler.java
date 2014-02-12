package com.tacitknowledge.slowlight.embedded;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

/**
 * TODO: review java docs to reflect actual implementation
 *
 * User: witherspore
 * Date: 6/19/13
 * Time: 7:58 AM
 * InvocationHandler for proxied target which manages calls via a ThreadPoolExecutor and DegradationStrategy
 * <p/>
 * Key class that delays and fails calls to the proxied target depending on ThreadPoolExecution active thread
 * utilization rates and strategy rules
 */
public class DegradationProxyHandler implements MethodHandler
{
    /**
     * This is the target instance for passing service calls
     */
    private final Object target;
    /**
     * Handler to provide degradation logic.
     */
    private final DegradationHandler degradationHandler;

    public DegradationProxyHandler(final Object target, final DegradationHandler degradationHandler)
    {
        this.target = target;
        this.degradationHandler = degradationHandler;
    }

    /**
	 * Standard entry point for InvocationHandlers.
	 * 
	 * Handler checks if it should perform degradation. If not, simply passes
	 * through the call to the target. Otherwise it invokes the destination with
	 * degradation.
	 *
	 * @param o
	 *            The embedded instance. Note this is not the target instance
	 * @param overridden
	 *            method to be invoked
	 * @param forwarder
	 *            not used at the moment.
	 * @param args
	 *            arguments to use during invocation
	 * @return target result or error object
	 * @throws Throwable
	 *             - generally this will be a the configured random exception,
	 *             but may be an InvocationTarget, Execution, or Interrupted
	 */
    @Override
    public Object invoke(final Object o, final Method overridden, final Method forwarder, final Object[] args) throws Throwable
    {
		TargetCallback targetCallback = new TargetCallback() {
				@Override
				public Object execute() throws Exception {
					return overridden.invoke(target, args);
				}
		};

		if (degradationHandler.isMethodExcluded(overridden)) {
			return degradationHandler.callDirectly(targetCallback);
		} else {
			return degradationHandler.invoke(targetCallback);
		}
    }


}
