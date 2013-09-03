package com.tacitknowledge.perf.degradation.proxy;

/**
 * User: witherspore
 * Date: 6/19/13
 * Time: 10:39 AM
 * Special exception to throw when DegradationStrategy indicates queues should timeout under load
 */
public class QueueTimeoutException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public QueueTimeoutException() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    public QueueTimeoutException(String s) {
        super(s);
    }
    /**
     * {@inheritDoc}
     */
    public QueueTimeoutException(String s, Throwable throwable) {
        super(s, throwable);
    }
    /**
     * {@inheritDoc}
     */
    public QueueTimeoutException(Throwable throwable) {
        super(throwable);
    }
}
