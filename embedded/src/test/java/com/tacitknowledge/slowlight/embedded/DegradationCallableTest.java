package com.tacitknowledge.slowlight.embedded;

import com.tacitknowledge.slowlight.embedded.stubs.StubbedServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * User: witherspore
 * Date: 9/3/13
 * Time: 11:42 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class DegradationCallableTest {

    @Mock
    private DegradationStrategy degradationStrategy;

    @Mock
    private DegradationHandler degradationHandler;

    @Mock
    private DegradationPlan degradationPlan;

    @Mock
    private ThreadLocalUtil threadLocalUtil;

    @Before
    public void setup()
    {
        when(degradationStrategy.generateDegradationPlan(degradationHandler)).thenReturn(degradationPlan);
    }

    @Test
    public void testFastFailure() throws Exception {
        final StubbedServiceImpl target = new StubbedServiceImpl();

        when(degradationPlan.getFastFail()).thenReturn(FastFail.TRUE);
        when(degradationPlan.hasPlannedFailure()).thenReturn(Boolean.TRUE);
        when(degradationPlan.fail()).thenReturn(1);

        DegradationCallable callable = new DegradationCallable(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return target.callService();
            }
        }, degradationStrategy, degradationHandler);

        Assert.assertEquals(1, callable.call());
        verify(degradationPlan).getFastFail();
        verify(degradationPlan).hasPlannedFailure();
        verify(degradationPlan).fail();
        verify(degradationPlan,never()).getDelay();
    }
    @Test
    public void testDelayedFailure() throws Exception {
        final StubbedServiceImpl target = new StubbedServiceImpl();

        when(degradationPlan.getFastFail()).thenReturn(FastFail.FALSE);
        when(degradationPlan.hasPlannedFailure()).thenReturn(Boolean.TRUE);
        when(degradationPlan.fail()).thenReturn(1);

        DegradationCallable callable = new DegradationCallable(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return target.callService();
            }
        }, degradationStrategy, degradationHandler);

        Assert.assertEquals(1, callable.call());
        verify(degradationPlan).getFastFail();
        verify(degradationPlan).hasPlannedFailure();
        verify(degradationPlan).fail();
        verify(degradationPlan).getDelay();
    }
    @Test
    public void testDelayWithNoFailure() throws Exception {
        final StubbedServiceImpl target = new StubbedServiceImpl();

        when(degradationStrategy.overrideResult(Matchers.<TargetCallback>any())).thenReturn(target.callService());

        when(degradationPlan.getDelay()).thenReturn(0L);
        when(degradationPlan.getFastFail()).thenReturn(FastFail.FALSE);
        when(degradationPlan.hasPlannedFailure()).thenReturn(Boolean.FALSE);

        DegradationCallable callable = new DegradationCallable(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return target.callService();
            }
        }, degradationStrategy, degradationHandler);

        Assert.assertEquals(0, callable.call());
        verify(degradationPlan).getFastFail();
        verify(degradationPlan).hasPlannedFailure();
        verify(degradationPlan,never()).fail();
        verify(degradationPlan).getDelay();
    }

    @Test
    public void callableShouldPropagateThreadLocalsBeforeGeneratingDegradationPlan() throws Exception
    {
        final DegradationCallable callable = new DegradationCallable(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return null;
            }
        }, degradationStrategy, degradationHandler);

        callable.setThreadLocalUtil(threadLocalUtil);
        callable.call();

        final InOrder inOrder = inOrder(threadLocalUtil, degradationStrategy);
        inOrder.verify(threadLocalUtil).propagateThreadLocals(Thread.currentThread());
        inOrder.verify(degradationStrategy).generateDegradationPlan(degradationHandler);
    }
}
