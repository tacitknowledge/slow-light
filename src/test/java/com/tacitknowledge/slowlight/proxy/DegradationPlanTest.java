package com.tacitknowledge.slowlight.proxy;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: witherspore
 * Date: 9/3/13
 * Time: 12:05 PM
 *
 * This class is mostly accessors.  Only the fail method has logic
 *
 * @see DegradationPlan
 */
public class DegradationPlanTest {

    @Test(expected = Exception.class)
    public void testFailWithException() throws Exception {
        DegradationPlan plan = new DegradationPlan(0L,new Exception("an exception"),new Object());
        plan.fail();
    }

    @Test
    public void testFailWithErrorObject() throws Exception {
        DegradationPlan plan = new DegradationPlan(0L,new Exception(),new Object(),true,FastFail.TRUE,
                FailurePriority.ERROR_OBJECT);
        Assert.assertNotNull(plan.fail());
    }

}
