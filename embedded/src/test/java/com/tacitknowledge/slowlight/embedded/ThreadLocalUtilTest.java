package com.tacitknowledge.slowlight.embedded;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ThreadLocalUtilTest
{
    private final ThreadLocalUtil threadLocalUtil = new ThreadLocalUtil();

    @Test
    public void utilShouldPropagateThreadLocals() throws InterruptedException
    {
        final String testValue = "test_value";
        final ThreadLocal<String> threadLocal = new ThreadLocal<String>();
        threadLocal.set(testValue);

        final Runnable testRunnable = new Runnable()
        {
            private final Thread parentThread = Thread.currentThread();

            @Override
            public void run()
            {
                try
                {
                    threadLocalUtil.propagateThreadLocals(parentThread);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

                assertThat(threadLocal.get(), is(equalTo(testValue)));
            }
        };

        final Thread testThread = new Thread(testRunnable);
        testThread.start();
        testThread.join();
    }
}
