package com.tacitknowledge.perf.degradation.proxy;

import static org.junit.Assert.*;

import com.tacitknowledge.perf.degradation.proxy.stubs.StubbedService;
import com.tacitknowledge.perf.degradation.proxy.stubs.StubbedServiceImpl;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

/**
 * User: mshort
 * Date: 9/3/13
 * Time: 12:12 PM
 */
public class DefaultDegradationStrategyTest {

    @Test
    public void testShouldSkipDegradation() {
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0);
        assertTrue(defaultDegradationStrategy.shouldSkipDegradation());
    }
    @Test
    public void testIsMethodExcluded() throws NoSuchMethodException {
        //check that if no methods configured, all are included
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0);
        assertFalse(defaultDegradationStrategy.isMethodExcluded(StubbedService.class.getMethod("callService")));

        //check that method is configured, only it is included
        defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0,new Method[] {
                StubbedService.class.getMethod("callOtherService")
        });
        assertTrue(defaultDegradationStrategy.isMethodExcluded(StubbedService.class.getMethod("callService")));
        assertFalse(defaultDegradationStrategy.isMethodExcluded(StubbedService.class.getMethod("callOtherService")));
    }
    @Test
    public void testGenerateRandomException() {
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0,
                new Class[]{
                    RuntimeException.class
        });
        assertNotNull(defaultDegradationStrategy.generateRandomException());

        defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0,
                new Class[]{

        });
        assertNull(defaultDegradationStrategy.generateRandomException());
    }
    @Test
    public void testGetRandomizedServiceDemandTime() {
        final long serviceDemandTime = 1000L;
        DefaultDegradationStrategy defaultDegradationStrategy
                = new DefaultDegradationStrategy(serviceDemandTime, serviceDemandTime,1.0);

        //should be between 0.75 and 1.25 of service demand time
        assertTrue(0.74 * serviceDemandTime < defaultDegradationStrategy.getRandomizedServiceDemandTime()
                && 1.26 * serviceDemandTime > defaultDegradationStrategy.getRandomizedServiceDemandTime());
    }
    @Test
    public void testFindUtilizationThresholdForPassRate() {
        final long serviceDemandTime = 1000L;
        DefaultDegradationStrategy defaultDegradationStrategy
                = new DefaultDegradationStrategy(serviceDemandTime, serviceDemandTime,1.1);
        Double threshold = defaultDegradationStrategy.findUtilizationThresholdForPassRate();
        assertEquals(new Double(1.0),threshold);

        defaultDegradationStrategy
                = new DefaultDegradationStrategy(serviceDemandTime, serviceDemandTime,0.5);
        threshold = defaultDegradationStrategy.findUtilizationThresholdForPassRate();
        assertEquals(new Double(Math.log(0.5 * (Math.exp(1) - 1) + 1)),threshold);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOverrideResultNoMethodException() throws Exception {
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0);
        defaultDegradationStrategy.overrideResult(new Object(), StubbedService.class.getMethod("callService"), new Object[]{});
    }
    @Test(expected = IllegalArgumentException.class)
    public void testOverrideResultBadArgumentException() throws Exception {
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0);
        defaultDegradationStrategy.overrideResult(new Object(), Object.class.getMethod("toString"), new Object[]{
                new Long(1L)
        });
    }
    @Test
    public void testGeneratePlanWithShouldSkip() throws Exception {
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(0L,0L,1.0);
        DegradationHandler degradationHandler = mock(DegradationHandler.class);
        when(degradationHandler.getPercentUtilized()).thenReturn(1.0);
        DegradationPlan plan = defaultDegradationStrategy.generateDegradationPlan(degradationHandler);
        assertFalse(plan.hasPlannedFailure());
        assertEquals(new Long(0L), plan.getDelay());
    }
    @Test
    public void testGeneratePlanWithErrorObject() throws Exception {
        DefaultDegradationStrategy defaultDegradationStrategy = new DefaultDegradationStrategy(1L,1L,0.0,
                new Class[]{},new Object(),FailurePriority.ERROR_OBJECT,FastFail.FALSE,false);
        DegradationHandler degradationHandler = mock(DegradationHandler.class);
        when(degradationHandler.getPercentUtilized()).thenReturn(1.0);
        DegradationPlan plan = defaultDegradationStrategy.generateDegradationPlan(degradationHandler);
        assertTrue(plan.hasPlannedFailure());
        //should return an object
        assertNotNull(plan.fail());
    }


}
