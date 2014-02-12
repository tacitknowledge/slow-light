package com.tacitknowledge.slowlight.embedded;

import java.lang.reflect.Method;

/**
 * User: witherspore
 * Date: 6/19/13
 * Time: 9:04 AM
 * Core configuration interface for degradation behavior. DegradationProxyHandler and the DegradationPlan query
 * this interface to determine behavior
 *
 * @see DegradationHandler
 * @see DegradationPlan
 */
public interface DegradationStrategy {

    /**
     *
     * @return value, in millis, for any base delay in embedded response
     */
    Long getServiceDemandTime();

    /**
     * Rough percentage under the scalability degradation curve of experiencing a successful response
     * @return value, between 0 and 1, that represents the chance of not 'failing'
     */
    Double getPassRate();

    /**
     * How long to delay the actual embedded call, generally randomized as a number between serviceDemandTIme and
     * serviceTimeout
     * @return  value in millis for the delay
     */
    Long getRandomizedServiceDemandTime();

    /**
     * How long, approximately, before service should timeout.
     * @return
     */
    Long getServiceTimeout();

    /**
     * error object to return if FailurePriority was ERROR_OBJECT in the degradation plan
     * @see DegradationPlan
     * @return
     */
    Object getErrorObject();

    /**
     * Creates a DegradationPlan for the specific call.
     * @param handler
     * @return
     */
    DegradationPlan generateDegradationPlan(DegradationHandler handler);

    /**
     * Tries to randomly generate an exception if the call did not pass.
     * @return an appropriate exception for the service interface
     */
    Exception generateRandomException();

    /**
     *
     * @return  true or false if handler should timeout embedded calls
     */
    Boolean isTimeoutQueues();

    /**
     * What to return when a successful call is made
     * @param targetCallback to be called
     * @return object for a successful call.  Typically, this just calls the object embedded
     * @throws Exception
     */
    Object overrideResult(final TargetCallback targetCallback) throws Exception;

    /**
     *
     * @return true if the embedded should skip degradation and call normally
     */
    Boolean shouldSkipDegradation();

    /**
     * Is the specific method on the embedded configured to be bypassed and called normally?
     *
     * @param method
     * @return true if the method should not be proxied
     * @see DefaultDegradationStrategy
     */
    Boolean isMethodExcluded(Method method);
}
