package com.tacitknowledge.perf.degradation.proxy;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/19/13
 * Time: 9:04 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DegradationStrategy {
    Long getServiceDemandTime();
    
    Double getPassRate();

    Long getRandomizedServiceDemandTime();

    Long getServiceTimeout();

    Object getErrorObject();

    DegradationPlan generateDegradationPlan(DegradationHandler handler);

    Exception generateRandomException();

    Boolean isTimeoutQueues();

    Object overrideResult(Method method, Object target, Object[] args) throws Exception;

    Boolean shouldSkip();

    Boolean isMethodExcluded(Method method);
}
