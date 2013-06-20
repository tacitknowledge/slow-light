package com.tacitknowledge.perf.degradation.proxy;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/19/13
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueueTimeoutException extends RuntimeException {
    public QueueTimeoutException() {
        super();
    }

    public QueueTimeoutException(String s) {
        super(s);
    }

    public QueueTimeoutException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public QueueTimeoutException(Throwable throwable) {
        super(throwable);
    }
}
