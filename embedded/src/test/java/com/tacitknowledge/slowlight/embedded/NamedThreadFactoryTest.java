package com.tacitknowledge.slowlight.embedded;

import org.junit.Test;

import java.util.concurrent.ThreadFactory;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: witherspore
 * Date: 6/21/13
 * Time: 9:54 AM
 */
public class NamedThreadFactoryTest {


    @Test
    public void testThreadNameAndThreadGroupName() {
        final String factoryName = "<factoryname>";
        ThreadFactory threadFactory = new NamedThreadFactory(factoryName);

        final Runnable runnable = new Runnable() {
            public void run() {

            }
        };

        final Thread thread = threadFactory.newThread(runnable);
        SecurityManager s = System.getSecurityManager();
        ThreadGroup group = (s != null) ? s.getThreadGroup() :
                     Thread.currentThread().getThreadGroup();
        final int priority = 5;
        //should contain parent group name and group # for factory
        String expectedThreadGroupName = NamedThreadFactory.PARENTGROUP
                + group.getName()
                + NamedThreadFactory.GROUPNAME
                + factoryName
                + 1;

        assertEquals("thread name problem",
                "Thread"
                        + "["
                        + NamedThreadFactory.THREADFACTORY
                        + factoryName
                        + NamedThreadFactory.GROUPNUMBER
                        + 1 //should be the first factory pool for first factory instance
                        + NamedThreadFactory.THREAD
                        + 1 //first thread, so 1
                        + "],"
                        + priority
                        + ","
                        + expectedThreadGroupName
                        + "]",
                thread.toString());


    }

}
