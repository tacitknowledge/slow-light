package com.tacitknowledge.slowlight.embedded;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

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
     * <p/>
     * 1. Handler checks the strategy to see if it should perform degradation. If not, simply passes through the call
     * to the target
     * 2. Creates a Callable, DegradationCallable.java, and submits it to the ThreadPoolExecutor
     * 3. Handles the future.get appropriately with or without timeouts as specified in the DegradationStrategy
     *
     * @param proxy  The embedded instance.  Note this is not the target instance
     * @param method method to be invoked
     * @param args   arguments to use during invocation
     * @return target result or error object
     * @throws Throwable - generally this will be a the configured random exception, but may be an InvocationTarget, Execution, or Interrupted
     * @see java.lang.reflect.InvocationHandler
     */
    @Override
    public Object invoke(final Object o, final Method overridden, final Method forwarder, final Object[] args) throws Throwable
    {
        // TODO: refactor this proxy handler to filter degradation excluded methods, see DegradationStrategy.isMethodExcluded(method)

        return degradationHandler.invoke(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return forwarder.invoke(target, args);
            }
        });
    }
}
