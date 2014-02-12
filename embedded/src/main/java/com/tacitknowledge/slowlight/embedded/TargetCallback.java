package com.tacitknowledge.slowlight.embedded;

/**
 * Implementation of this target callback interface is passed to a degradation handler {@link DegradationHandler},
 * allowing degradation handler to call back underlying (target) service logic.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public interface TargetCallback
{
    /**
     * Executes the callback logic.
     *
     * @return the callback result object
     * @throws Exception if any occurs during callback execution
     */
    Object execute() throws Exception;
}
