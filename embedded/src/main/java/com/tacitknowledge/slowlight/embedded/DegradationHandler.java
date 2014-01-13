package com.tacitknowledge.slowlight.embedded;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * DegradationHandler for target callback which manages calls via a ThreadPoolExecutor and DegradationStrategy.
 * <p/>
 * Key class that delays and fails calls to the target callback depending on ThreadPoolExecution active thread
 * utilization rates and strategy rules.
 *
 * @author witherspore, Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class DegradationHandler
{
    /**
     * ThreadPoolExecutor which runs the threads with sleep timers
     */
    private final ThreadPoolExecutor executorService;
    /**
     * Strategy defining rules for setting up DegradationPlans.
     */
    private final DegradationStrategy degradationStrategy;

    public DegradationHandler(ThreadPoolExecutor executorService,
                              DegradationStrategy degradationStrategy)
    {
        this.executorService = executorService;
        this.degradationStrategy = degradationStrategy;
    }

    /**
     * The percentage of threads running/active out of max in the thread pool
     *
     * @return percentage between 0.0 and 1.0
     */
    public double getPercentUtilized()
    {
        final int activeCount = Math.max(executorService.getActiveCount(), 0);
        final int maxSize = executorService.getMaximumPoolSize();

        return (double) activeCount / (double) maxSize;
    }

    /**
     * 1. Handler checks the strategy to see if it should perform degradation. If not, simply passes through the call to the target
     * 2. Creates a Callable, DegradationCallable.java, and submits it to the ThreadPoolExecutor
     * 3. Handles the future.get appropriately with or without timeouts as specified in the DegradationStrategy
     *
     * @param targetCallback to be called by the handler
     * @return target result or error object
     * @throws Throwable - generally this will be a the configured random exception, but may be an InvocationTarget, Execution,
     * or Interrupted
     */
    public Object invoke(final TargetCallback targetCallback) throws Throwable
    {

        if (degradationStrategy.shouldSkipDegradation())
        {
            return callDirectly(targetCallback);
        }

        try
        {
            final Callable callable = new DegradationCallable(targetCallback, degradationStrategy, this);

            //this may throw an exception, timeout the future, or wait forever for the result
            //  can modify the result as well.
            final Future future = executorService.submit(callable);

            //handle ThreadPoolExecutor timeouts if configured.  Usually this is a Callable in the queue,
            // but could possibly be active. default is false.
            if (degradationStrategy.isTimeoutQueues())
            {
                try
                {
                    return future.get(degradationStrategy.getServiceTimeout(), TimeUnit.MILLISECONDS);
                }
                catch (TimeoutException e)
                {
                    throw new QueueTimeoutException("Request in queue timed out", e);
                }
            }
            else
            {
                return future.get();
            }

        }
        catch (ExecutionException e)
        {
            throw e.getCause();
        }
    }

    /**
     * Convenience method for directly invoking the target callback without going through a Callable
     *
     * @param targetCallback to be directly invoked
     * @return Object from target
     * @throws Exception
     */
    public Object callDirectly(final TargetCallback targetCallback) throws Exception
    {
        return targetCallback.execute();
    }

    /**
     * @return DegradationStrategy configured for this handler at construction
     */
    public DegradationStrategy getDegradationStrategy()
    {
        return degradationStrategy;
    }

    /**
     * @return ThreadPoolExecutor configured for this handler at construction
     */
    public ThreadPoolExecutor getExecutorService()
    {
        return executorService;
    }
}
