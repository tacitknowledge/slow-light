package com.tacitknowledge.slowlight.embedded;

/**
 * User: witherspore
 * Date: 6/19/13
 * Time: 10:17 AM
 * Should the embedded return an error object or throw an exception
 */
public enum FailurePriority {
    /**
     * return an error object if possible. if not available, will fallback to throwing an exception
     */
    ERROR_OBJECT,
    /**
     * throw an exception.
     */
    EXCEPTION;
}
