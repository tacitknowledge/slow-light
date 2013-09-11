package com.tacitknowledge.slowlight.embedded;

/**
 * User: witherspore
 * Date: 6/19/13
 * Time: 10:13 AM
 * Enum to set whether new calls should fail immediately if planned for failure
 *
 * This is to simulate a service where its blocking queue and pool are full and the call is immediately rejected
 *
 * @see java.util.concurrent.RejectedExecutionHandler
 * @see java.util.concurrent.RejectedExecutionException
 */
public enum FastFail {
    /**
     * Fail immediately if the DegradationPlan indicates
     * @see DegradationPlan
     */
    TRUE,
    /**
     * Wait until time out, then fail
     * @see DegradationPlan
     */
    FALSE;
}
