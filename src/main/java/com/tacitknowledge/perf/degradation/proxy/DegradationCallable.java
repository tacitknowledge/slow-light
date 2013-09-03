package com.tacitknowledge.perf.degradation.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * User: mshort
 * Date: 9/3/13
 * Time: 9:28 AM
 * <p/>
 * Callable responsible for delaying thread execution and throwing or returning errors as per the degradation
 * strategy's generateDegradationPlan method and querying the Thread Pool in the handler.
 */
public class DegradationCallable implements Callable {
    /**
     * method to execute
     */
    final private Method method;
    /**
     * Object or service instance for the method
     */
    final private Object target;
    /**
     * Strategy for rules
     */
    final private DegradationStrategy degradationStrategy;
    /**
     * Callback for acquiring the utilization of the handler thread pool
     */
    final private DegradationHandler handlerCallback;

    /**
     * Args to use on Method for target
     */
    final Object[] args;


    public DegradationCallable(Method method,
                               Object target,
                               DegradationStrategy degradationStrategy,
                               DegradationHandler handlerCallback,
                               Object[] args) {
        this.method = method;
        this.target = target;
        this.degradationStrategy = degradationStrategy;
        this.handlerCallback = handlerCallback;
        this.args = args;
    }

    /**
     * Callable call implementation for degraded calls
     *
     * @return either the normal call result or the error object
     * @throws Exception
     */
    public Object call() throws Exception {
        DegradationPlan plan = degradationStrategy.generateDegradationPlan(handlerCallback);

        //readibility is improved with this short cicruit
        if (FastFail.TRUE == plan.getFastFail() && plan.hasPlannedFailure()) {
            return plan.fail();
        }
        //if not fast fail and planned to fail, sleep a bit
        Thread.currentThread().sleep(plan.getDelay());

        //slow fail if needed with Exception or errorObject
        if (plan.hasPlannedFailure()) {
            return plan.fail();
        }
        //return actual pass thru callService or an override if no failure after delay
        return degradationStrategy.overrideResult(target, method, args);
    }

}
