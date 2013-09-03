package com.tacitknowledge.perf.degradation.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * User: witherspore
 * Date: 6/19/13
 * Time: 7:58 AM
 * InvocationHandler for proxied target which manages calls via a ThreadPoolExecutor and DegradationStrategy
 * <p/>
 * Key class that delays and fails calls to the proxied target depending on ThreadPoolExecution active thread
 * utilization rates and strategy rules
 */
public class DegradationHandler implements InvocationHandler {
    /**
     * This is the target instance for passing service calls
     */
    final private Object target;
    /**
     * ThreadPoolExecutor which runs the threads with sleep timers
     */
    final private ThreadPoolExecutor executorService;
    /**
     * Strategy defining rules for setting up DegradationPlans.
     */
    final private DegradationStrategy degradationStrategy;

    public DegradationHandler(Object target,
                              ThreadPoolExecutor executorService,
                              DegradationStrategy degradationStrategy) {
        this.target = target;
        this.executorService = executorService;
        this.degradationStrategy = degradationStrategy;
    }

    /**
     * The percentage of threads running/active out of max in the thread pool
     *
     * @return percentage between 0.0 and 1.0
     */
    public double getPercentUtilized() {
        final int activeCount = Math.max(executorService.getActiveCount(), 0);
        final int maxSize = executorService.getMaximumPoolSize();
        final double percentUsed = (double) activeCount / (double) maxSize;
        return percentUsed;
    }

    /**
     * Standard entry point for InvocationHandlers.
     * <p/>
     * 1. Handler checks the strategy to see if it should perform degradation. If not, simply passes through the call
     * to the target
     * 2. Creates a Callable, DegradationCallable.java, and submits it to the ThreadPoolExecutor
     * 3. Handles the future.get appropriately with or without timeouts as specified in the DegradationStrategy
     *
     * @param proxy  The proxy instance.  Note this is not the target instance
     * @param method method to be invoked
     * @param args   arguments to use during invocation
     * @return target result or error object
     * @throws Throwable - generally this will be a the configured random exception, but may be an InvocationTarget, Execution, or Interrupted
     * @see java.lang.reflect.InvocationHandler
     */
    public Object invoke(final Object proxy, final Method method,
                         final Object[] args) throws Throwable {

        if (degradationStrategy.shouldSkipDegradation() || degradationStrategy.isMethodExcluded(method)) {
            return callDirectly(target, method, args);
        }

        try {
            Callable callable = new DegradationCallable(method, target, degradationStrategy, this, args);

            //this may throw an exception, timeout the future, or wait forever for the result
            //  can modify the result as well.
            Future future = executorService.submit(callable);

            //handle ThreadPoolExecutor timeouts if configured.  Usually this is a Callable in the queue,
            // but could possibly be active. default is false.
            if (degradationStrategy.isTimeoutQueues()) {
                try {
                    return future.get(degradationStrategy.getServiceTimeout(), TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    throw new QueueTimeoutException("Request in queue timed out for degradation mock on " + target.getClass(), e);
                }
            } else {
                return future.get();
            }

        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    /**
     * Convenience method for directly invoking the target method without going through a Callable
     *
     * @param target
     * @param method
     * @param args
     * @return Object from target
     * @throws Exception
     */
    public Object callDirectly(Object target, Method method, Object[] args) throws Exception {
        ProxyUtil proxyUtil = new ProxyUtil();
        return proxyUtil.invokeTarget(target, method, args);
    }

    /**
     * @return DegradationStrategy configured for this handler at construction
     */
    public DegradationStrategy getDegradationStrategy() {
        return degradationStrategy;
    }

    /**
     * @return ThreadPoolExecutor configured for this handler at construction
     */
    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }


}
