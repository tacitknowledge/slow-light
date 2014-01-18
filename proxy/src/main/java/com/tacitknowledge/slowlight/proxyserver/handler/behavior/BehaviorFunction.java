package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import java.util.Map;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public interface BehaviorFunction
{
    /**
     * Evaluates a function using specified parameters.
     *
     * @param params function parameters
     * @return evaluation result
     */
    Object evaluate(Map<String, ?> params);

    /**
     * Checks if function should be evaluated (ex. it can check if function is applicable for a given time interval).
     *
     * @return true if function should be evaluated, otherwise false
     */
    boolean shouldEvaluate();
}
