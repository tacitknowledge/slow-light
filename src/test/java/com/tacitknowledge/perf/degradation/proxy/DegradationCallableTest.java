package com.tacitknowledge.perf.degradation.proxy;

import com.tacitknowledge.perf.degradation.proxy.stubs.StubbedServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

import org.mockito.Matchers;
import org.mockito.Mock;

import java.lang.reflect.Method;


/**
 * User: witherspore
 * Date: 9/3/13
 * Time: 11:42 AM
 */
public class DegradationCallableTest {

    private DegradationStrategy degradationStrategy;
    private DegradationHandler degradationHandler = mock(DegradationHandler.class);
    private Object[] args = new Object[]{};
    private DegradationPlan degradationPlan;

    @Test
    public void testFastFailure() throws Exception {
        final StubbedServiceImpl target = new StubbedServiceImpl();
        final Method method = target.getClass().getMethod("callService");
        degradationPlan = mock(DegradationPlan.class);
        degradationStrategy = mock(DegradationStrategy.class);
        when(degradationStrategy.generateDegradationPlan(degradationHandler)).thenReturn(degradationPlan);

        when(degradationPlan.getFastFail()).thenReturn(FastFail.TRUE);
        when(degradationPlan.hasPlannedFailure()).thenReturn(Boolean.TRUE);
        when(degradationPlan.fail()).thenReturn(new Integer(1));

        DegradationCallable callable = new DegradationCallable(method, target,degradationStrategy,
                degradationHandler,args);

        Assert.assertEquals(new Integer(1), callable.call());
        verify(degradationPlan).getFastFail();
        verify(degradationPlan).hasPlannedFailure();
        verify(degradationPlan).fail();
        verify(degradationPlan,never()).getDelay();
    }
    @Test
    public void testDelayedFailure() throws Exception {
        final StubbedServiceImpl target = new StubbedServiceImpl();
        final Method method = target.getClass().getMethod("callService");
        degradationPlan = mock(DegradationPlan.class);
        degradationStrategy = mock(DegradationStrategy.class);
        when(degradationStrategy.generateDegradationPlan(degradationHandler)).thenReturn(degradationPlan);

        when(degradationPlan.getFastFail()).thenReturn(FastFail.FALSE);
        when(degradationPlan.hasPlannedFailure()).thenReturn(Boolean.TRUE);
        when(degradationPlan.fail()).thenReturn(new Integer(1));

        DegradationCallable callable = new DegradationCallable(method, target,degradationStrategy,
                degradationHandler,args);

        Assert.assertEquals(new Integer(1), callable.call());
        verify(degradationPlan).getFastFail();
        verify(degradationPlan).hasPlannedFailure();
        verify(degradationPlan).fail();
        verify(degradationPlan).getDelay();
    }
    @Test
    public void testDelayWithNoFailure() throws Exception {
        final StubbedServiceImpl target = new StubbedServiceImpl();
        final Method method = target.getClass().getMethod("callService");
        degradationPlan = mock(DegradationPlan.class);
        degradationStrategy = mock(DegradationStrategy.class);
        when(degradationStrategy.generateDegradationPlan(degradationHandler)).thenReturn(degradationPlan);
        when(degradationStrategy.overrideResult(any(), Matchers.<Method>any(), Matchers.<Object[]>any())).thenReturn(
                target.callService()
        );

                when(degradationPlan.getDelay()).thenReturn(0L);
        when(degradationPlan.getFastFail()).thenReturn(FastFail.FALSE);
        when(degradationPlan.hasPlannedFailure()).thenReturn(Boolean.FALSE);

        DegradationCallable callable = new DegradationCallable(method, target,degradationStrategy,
                degradationHandler,args);

        Assert.assertEquals(new Integer(0), callable.call());
        verify(degradationPlan).getFastFail();
        verify(degradationPlan).hasPlannedFailure();
        verify(degradationPlan,never()).fail();
        verify(degradationPlan).getDelay();
    }

}
