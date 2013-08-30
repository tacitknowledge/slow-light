package com.tacitknowledge.perf.degradation.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/19/13
 * Time: 7:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDegradationStrategy implements DegradationStrategy {

    public static final double FIFTY_PERCENT = 0.5;
    public static final double TWO = 2.0;
    public static final double ONE_HUNDRED_PERCENT = 1.0;
    public static final double ONE_QUARTER = 0.25;
    public static final String GENERATED_BY_DEGRADATION_PROXY = "Generated by degradation proxy.";

    final private Long serviceDemandTime;
    final private Long serviceTimeout;
    final private double passRate;
    final private Class<Exception>[] randomExceptions;
    final private Object errorObject;
    final private FailurePriority failurePriority;
    final private FastFail fastFail;
    final private List<Method> degradedMethods;
    //todo - mws - maybe move this? really part of handler
    final private Boolean timeoutQueues;

    public DefaultDegradationStrategy(Long serviceDemandTime,
                                      Long serviceTimeout,
                                      double passRate,
                                      Class<Exception>[] randomExceptions,
                                      Object errorObject,
                                      FailurePriority failurePriority,
                                      FastFail fastFail,
                                      Boolean timeoutQueues,
                                      Method[] degradedMethods) {
        this.serviceDemandTime = Math.max(0L,serviceDemandTime);
        this.serviceTimeout = Math.max(serviceDemandTime,serviceTimeout);
        this.passRate = passRate;
        this.randomExceptions = randomExceptions;
        this.errorObject = errorObject;
        this.failurePriority = failurePriority;
        this.fastFail = fastFail;
        this.timeoutQueues = timeoutQueues;
        this.degradedMethods = Arrays.asList(degradedMethods);

    }
    public DefaultDegradationStrategy(Long serviceDemandTime,
                                      Long serviceTimeout,
                                      double passRate,
                                      Class<Exception>[] randomExceptions,
                                      Object errorObject,
                                      FailurePriority failurePriority,
                                      FastFail fastFail,
                                      Boolean timeoutQueues
                                      ) {
        this(
                serviceDemandTime,
                serviceTimeout,
                passRate,
                randomExceptions,
                errorObject,
                failurePriority,
                fastFail,
                timeoutQueues,
                new Method[]{}
            );

    }


    public DefaultDegradationStrategy(Long serviceDemandTime,
                                      Long serviceTimeout,
                                      double passRate,
                                      Class<Exception>[] randomExceptions) {
        this(serviceDemandTime,
                serviceTimeout,
                passRate,
                randomExceptions,
                new Method[] {}
        );
    }
    public DefaultDegradationStrategy(Long serviceDemandTime,
                                      Long serviceTimeout,
                                      double passRate,
                                      Class<Exception>[] randomExceptions, Method[] degradedMethods) {
        this(serviceDemandTime,
                serviceTimeout,
                passRate,
                randomExceptions,
                null,
                FailurePriority.EXCEPTION,
                FastFail.FALSE,
                Boolean.FALSE,
                degradedMethods
        );
    }

    public DefaultDegradationStrategy(Long serviceDemandTime, Long serviceTimeout, double passRate) {
        this(serviceDemandTime,
                serviceTimeout,
                passRate,
                new Method[]{}
        );
    }
    public DefaultDegradationStrategy(Long serviceDemandTime, Long serviceTimeout, double passRate,
                                      Method[] degradedMethods) {
        this(serviceDemandTime,
                serviceTimeout,
                passRate,
                new Class[]{}, degradedMethods
        );
    }

    public Long getServiceDemandTime() {
        return serviceDemandTime;
    }

    public Long getServiceTimeout() {
        return serviceTimeout;
    }


    private Long getTimeoutMinusServiceDemand() {
        final double result = (serviceTimeout - serviceDemandTime);
        return Math.max(0, Math.round(result));
    }

    public Long getRandomizedServiceDemandTime() {
        double betweenNegativeOneQuarterandOneQuarter = randomInRange(-0.25,0.25);
        return Math.round(getServiceDemandTime() * (ONE_HUNDRED_PERCENT + betweenNegativeOneQuarterandOneQuarter));
    }

    public DegradationPlan generateDegradationPlan(DegradationHandler handler) {
        //solve for area under exp curve such that errorThresholdPercent represents the point accurately
        // for instance, 10% error rate will have a passRate of approx. exp(0.9347) == 90%

        Long adjustedResponseTime = calculateAdjustedResponseTime(handler);
        Boolean shouldFail = checkForFailure(adjustedResponseTime);

        final DegradationPlan degradationPlan = new DegradationPlan(adjustedResponseTime,
                generateRandomException(),
                getErrorObject(),
                shouldFail,
                fastFail,
                failurePriority);
        return degradationPlan;
    }

    private Long calculateAdjustedResponseTime(DegradationHandler handler) {
        return Math.max(getRandomizedServiceDemandTime(),Math.round(
                (ONE_HUNDRED_PERCENT + randomInRange(-0.25,0.25))
                * getServiceDemandTime()
                *  Math.exp(handler.getPercentUtilized())
                * handler.getPercentUtilized() * getServiceTimeout()/(getServiceDemandTime() * Math.exp(1))
         ));

    }


    private double randomInRange(double min, double max) {
      Random random = new Random();
      double range = max - min;
      double scaled = random.nextDouble() * range;
      double shifted = scaled + min;
      return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

    private double findExponentiationThresholdForPassRate() {
        return Math.exp(findUtilizationThresholdForPassRate());
    }
    private double findUtilizationThresholdForPassRate() {
        final double utilThreshold = (passRate >= ONE_HUNDRED_PERCENT)
                ? ONE_HUNDRED_PERCENT
                : Math.log(passRate * (Math.exp(1) - 1) + 1);
        return utilThreshold;
    }

    protected Boolean checkForFailure(double exponentialDelayFactor) {

        if (passRate >= 1.0)
            return Boolean.FALSE;
        Boolean shouldFail = Boolean.FALSE;
        if (exponentialDelayFactor > getServiceTimeout() * findUtilizationThresholdForPassRate()) {
            shouldFail = Boolean.TRUE;
        }

        return shouldFail;
    }


    public Object getErrorObject() {
        return errorObject;
    }


    public Exception generateRandomException() {
        Exception exception = null;
        if (randomExceptions.length != 0) {
            Integer index = (int) (Math.random() * (double) randomExceptions.length);
            try {
                for (Constructor constructor : randomExceptions[index].getConstructors()) {
                    if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(String.class))
                        exception = (Exception) constructor.newInstance(GENERATED_BY_DEGRADATION_PROXY);
                }
                if (exception == null) {
                    exception = randomExceptions[index].newInstance();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return exception;
    }


    public Object overrideResult(Method method, Object target, Object[] args) throws Exception {
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


    public Boolean isTimeoutQueues() {
        return timeoutQueues;
    }

    public Double getPassRate() {
        return passRate;
    }

    public Boolean shouldSkip() {
        return getPassRate() == 1.0 && getServiceDemandTime() == 0L && getServiceTimeout() == 0L;
    }

    /**
     * If degraded methods was empty, return false as all methods should be degraded.  If its non-empty, only return
     * false for methods matching the list.
     * @param method
     * @return false if method should not be degraded.
     */
    public Boolean isMethodExcluded(Method method) {
        //fast exit for none specified.  should always degrade when empty
        if (degradedMethods.isEmpty()) {
            return Boolean.FALSE;
        }
        //attempt to match method
        for (Method degradedMethod : degradedMethods) {
            if (method.getName().equals(degradedMethod.getName())) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;

    }
}
