package com.tacitknowledge.slowlight.embedded;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by IntelliJ IDEA.
 * User: witherspore
 * Date: 6/21/13
 * Time: 9:54 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class NamedThreadFactoryTest {

    private static final String FACTORY_NAME = "<factoryname>";

    private static final int FACTORY_GROUP_NUMBER = 1;

    private NamedThreadFactory threadFactory;

    @Before
    public void setup()
    {
        threadFactory = spy(new NamedThreadFactory(FACTORY_NAME));
        doReturn(FACTORY_GROUP_NUMBER).when(threadFactory).getFactoryGroupNumber();
    }

    @Test
    public void testThreadNameAndThreadGroupName() {

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
                + FACTORY_NAME
                + 1;

        final String expected = "Thread"
                + "["
                + NamedThreadFactory.THREADFACTORY
                + FACTORY_NAME
                + NamedThreadFactory.GROUPNUMBER
                + FACTORY_GROUP_NUMBER //should be the first factory pool for first factory instance
                + NamedThreadFactory.THREAD
                + 1 //first thread, so 1
                + "],"
                + priority
                + ","
                + expectedThreadGroupName
                + "]";

        final String actual = thread.toString();

        assertEquals("thread name problem", expected, actual);
    }
}
