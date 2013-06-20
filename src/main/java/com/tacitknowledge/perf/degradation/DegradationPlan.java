package com.tacitknowledge.perf.degradation;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/19/13
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class DegradationPlan {

    private Long delay;
    private Exception randomException;
    private Object errorObject;
    private Boolean shouldFail = Boolean.FALSE;
    private FastFail fastFail = FastFail.FALSE;
    private FailurePriority failurePriority = FailurePriority.EXCEPTION;

    public DegradationPlan(Long delay,
                           Exception randomException,
                           Object errorObject,
                           Boolean shouldFail,
                           FastFail fastFail,
                           FailurePriority failurePriority) {
        this.delay = delay;
        this.randomException = randomException;
        this.errorObject = errorObject;
        this.shouldFail = shouldFail;
        this.fastFail = fastFail;
        this.failurePriority = failurePriority;
    }

    public DegradationPlan(Long delay, Exception randomException, Object errorObject) {
        this(delay,randomException,errorObject,Boolean.FALSE,FastFail.FALSE,FailurePriority.EXCEPTION);
    }

    public Exception getRandomException() {
        return randomException;
    }

    public Long getDelay() {
        return delay;
    }

    private Boolean hasException() {
        return randomException != null;
    }

    private Boolean hasErrorObject() {
        return errorObject != null;
    }

    public Object getErrorObject() {
        return errorObject;
    }

    public Boolean hasPlannedFailure() {
        return shouldFail;
    }

    public Object fail() throws Exception {
        if (FailurePriority.EXCEPTION == getFailurePriority() && getRandomException() != null)
            throw getRandomException();
        return getErrorObject();
    }

    public FailurePriority getFailurePriority() {
        return failurePriority;
    }

    public FastFail getFastFail() {
        return fastFail;
    }
}
