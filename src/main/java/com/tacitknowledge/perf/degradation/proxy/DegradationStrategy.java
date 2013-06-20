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

    Long getRandomizedServiceDemandTime();

    Long getServiceTimeout();

    public Object getErrorObject();

    DegradationPlan generateDegradationPlan(DegradationHandler handler);

    Exception generateRandomException();

    Boolean isTimeoutQueues();

    public Object overrideResult(Method method, Object target, Object[] args) throws Exception;
}
