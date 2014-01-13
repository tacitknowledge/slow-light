package com.tacitknowledge.slowlight.embedded;

import java.util.concurrent.Callable;

/**
 * User: witherspore
 * Date: 9/3/13
 * Time: 9:28 AM
 * <p/>
 * Callable responsible for delaying thread execution and throwing or returning errors as per the degradation
 * strategy's generated DegradationPlan.
 */
public class DegradationCallable implements Callable {
    /**
     * Callback to execute
     */
    final private TargetCallback targetCallback;
    /**
     * Strategy for rules
     */
    final private DegradationStrategy degradationStrategy;
    /**
     * Callback for acquiring the utilization of the handler thread pool
     */
    final private DegradationHandler degradationHandler;

    /**
     * Reference to parent thread.
     */
    final private Thread parentThread;
    /**
     * Util class to be used for thread locals manipulations.
     */
    private ThreadLocalUtil threadLocalUtil = new ThreadLocalUtil();

    public DegradationCallable(TargetCallback targetCallback,
                               DegradationStrategy degradationStrategy,
                               DegradationHandler degradationHandler)
    {
        this.targetCallback = targetCallback;
        this.degradationStrategy = degradationStrategy;
        this.degradationHandler = degradationHandler;

        this.parentThread = Thread.currentThread();
    }

    /**
     * Callable call implementation for degraded calls
     *
     * @return either the normal call result or the error object
     * @throws Exception
     */
    public Object call() throws Exception
    {
        threadLocalUtil.propagateThreadLocals(parentThread);

        DegradationPlan plan = degradationStrategy.generateDegradationPlan(degradationHandler);

        //readability is improved with this short cicruit
        if (FastFail.TRUE == plan.getFastFail() && plan.hasPlannedFailure()) {
            return plan.fail();
        }
        //if not fast fail and planned to fail, sleep a bit
        Thread.sleep(plan.getDelay());

        //slow fail if needed with Exception or errorObject
        if (plan.hasPlannedFailure()) {
            return plan.fail();
        }
        //return actual pass thru callService or an override if no failure after delay
        return degradationStrategy.overrideResult(targetCallback);
    }

    /**
     * Sets util to be used for thread locals manipulations.
     *
     * @param threadLocalUtil
     */
    public void setThreadLocalUtil(final ThreadLocalUtil threadLocalUtil)
    {
        this.threadLocalUtil = threadLocalUtil;
    }
}
