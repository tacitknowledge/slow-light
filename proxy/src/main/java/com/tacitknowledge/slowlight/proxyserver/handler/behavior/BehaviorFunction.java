package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import java.util.Map;

/**
 * Determines an output parameter value based on input function parameters.
 *
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
    Object evaluate(final Map<String, ?> params);
}
