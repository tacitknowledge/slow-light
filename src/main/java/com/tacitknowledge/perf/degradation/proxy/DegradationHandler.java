package com.tacitknowledge.perf.degradation.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
* Created by IntelliJ IDEA.
* User: mshort
* Date: 6/19/13
* Time: 7:58 AM
* To change this template use File | Settings | File Templates.
*/
public class DegradationHandler implements InvocationHandler {
    final private Object target;
    final private ThreadPoolExecutor executorService;
    final private DegradationStrategy degradationStrategy;

    public DegradationHandler(Object target,
                              ThreadPoolExecutor executorService,
                              DegradationStrategy degradationStrategy) {
        this.target = target;
        this.executorService = executorService;
        this.degradationStrategy = degradationStrategy;
    }

    public double getPercentUtilized() {
        final int activeCount = Math.max(executorService.getActiveCount(), 0);
        final int maxSize = executorService.getMaximumPoolSize();
        final double percentUsed = (double)activeCount/(double)maxSize;
        return percentUsed;
    }

    @Override
    public Object invoke(final Object proxy, final Method method,
                         final Object[] args) throws Throwable {

        if (degradationStrategy.shouldSkip() || degradationStrategy.isMethodExcluded(method)) {
            return callDirectly(target,method,args);
        }

        try {
            Callable callable = new DegradationCallable(method,target,degradationStrategy,this,args);

            //this may throw an exception, timeout the future, or wait forever for the result
            //  can modify the result as well.
            Future future = executorService.submit(callable);

            //handle ThreadPoolExecutor timeouts if configured.  Usually this is a Callable in the queue,
            // but could possibly be active. default is false.
            if (degradationStrategy.isTimeoutQueues()) {
                try {
                    return future.get(degradationStrategy.getServiceTimeout(), TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    throw new QueueTimeoutException("Request in queue timed out for degradation mock on " + target.getClass(),e);
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


       public Object callDirectly(Object target, Method method,  Object[] args) throws Exception {
           try {
               return method.invoke(target, args);
           } catch (IllegalAccessException e) {
               throw e;
           } catch (IllegalArgumentException e) {
               throw e;
           } catch (InvocationTargetException e) {
               throw (Exception) e.getCause();
           }
       }

    public static class DegradationCallable implements Callable {
        final private Method method;
        final private Object target;
        final private DegradationStrategy degradationStrategy;
        final private DegradationHandler handlerCallback;
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

        public Object call() throws Exception {
                DegradationPlan plan = degradationStrategy.generateDegradationPlan(handlerCallback);

                //readibility is improved with this short cicruit
                if (FastFail.TRUE == plan.getFastFail() && plan.hasPlannedFailure()) {
                    return plan.fail();
                }
                //if not fast fail and planned to fail, sleep a bit
                Thread.currentThread().sleep(plan.getDelay());

                //slow fail if needed with Exception or Plans errorObject
                if (plan.hasPlannedFailure()) {
                    return plan.fail();
                }
            //return actual pass thru callService or an override
            return degradationStrategy.overrideResult(method,target,args);
        }


    }
}
