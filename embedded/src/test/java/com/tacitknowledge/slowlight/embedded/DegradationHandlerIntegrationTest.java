package com.tacitknowledge.slowlight.embedded;

import com.tacitknowledge.slowlight.embedded.stubs.StubbedService;
import com.tacitknowledge.slowlight.embedded.stubs.StubbedServiceErrorImpl;
import com.tacitknowledge.slowlight.embedded.stubs.StubbedServiceImpl;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: witherspore
 * Date: 6/18/13
 * Time: 8:53 AM
 *
 * Integration tests using a multi-threaded client driver to apply load on stubbed services wrapped in the
 * DegradationProxyHandler and DefaultDegradationStrategy
 *
 * This covers testing for the DegradationProxyHandler
 *
 * @see DefaultDegradationStrategy
 * @see DegradationHandler
 */
public class DegradationHandlerIntegrationTest {

    @Test
    public void testDegradationHandlerWhereLoadDoesNotExceedCapacity() throws Exception {
        long timestamp = System.currentTimeMillis();
        final int concurrentLoad = 3;
        final int capacity = 3;
        final long serviceDemandTime = 500L;
        final long serviceTimeout = 500L;
        final double passRate = 1.0;
        DefaultDegradationStrategy degradationStrategy = new DefaultDegradationStrategy(serviceDemandTime, serviceTimeout, passRate);

        final long totalTime = runServiceUnderLoad(timestamp, concurrentLoad, capacity, degradationStrategy);

        assertTrue("Should have been less than 750 ms, but was " + totalTime, totalTime < serviceDemandTime * 1.5);
    }

    @Test
    public void testDegradationHandlerWhereLoadExceedsCapacity() throws Exception {
        long timestamp = System.currentTimeMillis();
        final int concurrentLoad = 6;
        final int capacity = 3;
        final long serviceDemandTime = 500L;
        final long serviceTimeout = 500L;
        final double passRate = 1.0;
        DefaultDegradationStrategy degradationStrategy = new DefaultDegradationStrategy(serviceDemandTime, serviceTimeout, passRate);
        final long totalTime = runServiceUnderLoad(timestamp, concurrentLoad, capacity, degradationStrategy);
        final long minimumTimeWithQueueing = (long) (serviceDemandTime * 2 * 0.75);
        assertTrue("Should have been more than " + minimumTimeWithQueueing
                + " ms, but was " + totalTime, totalTime > minimumTimeWithQueueing);

    }

    @Test(expected = RuntimeException.class)
    public void testDegradationHandlerWithResponseTimeDegradationThatThrowsRuntime() throws Exception {
        long timestamp = System.currentTimeMillis();
        final int concurrentLoad = 20;
        final int capacity = 10;
        final long serviceDemandTime = 500L;
        final long serviceTimeout = 2 * serviceDemandTime;
        final double passRate = 0.90;
        DefaultDegradationStrategy degradationStrategy = new DefaultDegradationStrategy(serviceDemandTime, serviceTimeout,
                passRate,
                new Class[]{RuntimeException.class});
        runServiceUnderLoad(timestamp, concurrentLoad, capacity, degradationStrategy);
    }

    @Test(expected = QueueTimeoutException.class)
    public void testTimeoutQueues() throws Exception {
        long timestamp = System.currentTimeMillis();
        final int concurrentLoad = 60;
        final int capacity = 10;
        final long serviceDemandTime = 500L;
        final long serviceTimeout = serviceDemandTime;
        final double passRate = 1.0;
        final Class[] randomExceptions = {RuntimeException.class};
        DefaultDegradationStrategy degradationStrategy = new DefaultDegradationStrategy(serviceDemandTime, serviceTimeout, passRate,
                randomExceptions, null, FailurePriority.EXCEPTION, FastFail.FALSE, true);
        runServiceUnderLoad(timestamp, concurrentLoad, capacity, degradationStrategy);
    }


    @Test(expected = RuntimeException.class)
    public void testBubbleRuntimeExceptionThroughThreadPoolAndProxy()
            throws Throwable
    {
        final StubbedService stub = new StubbedServiceImpl() {
            @Override
            public Integer callService() {
                throw new RuntimeException("trial exception");
            }
        };
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        //Execute service with degradation handler
        DegradationHandler handler = new DegradationHandler(executorService, new DefaultDegradationStrategy(1L, 1L, 1.0));
        handler.invoke(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return stub.callService();
            }
        });
    }

    @Test(expected = ConnectException.class )
    public void testBubbleConnectExceptionThroughThreadPoolAndProxy()
            throws Throwable
    {
        final StubbedService stub = new StubbedServiceErrorImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        DegradationHandler handler = new DegradationHandler(executorService, new DefaultDegradationStrategy(1L, 1L, 1.0));
        handler.invoke(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return stub.callService();
            }
        });
    }
    @Test(expected = FileNotFoundException.class )
    public void testForceFileNotFoundExceptionFromClassArray()
            throws Throwable
    {
        final StubbedService stub = new StubbedServiceImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        DegradationHandler handler = new DegradationHandler(executorService,
                new DefaultDegradationStrategy(1L, 1L, 0.0, new Class[]{ FileNotFoundException.class }));
        handler.invoke(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return stub.callService();
            }
        });
    }

    @Test
    public void testReturnErrorObject()
            throws Throwable
    {
        final StubbedService stub = new StubbedServiceImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        final double passRate = 0.0;
        final Integer errorObject = 25;

        DegradationHandler handler = new DegradationHandler(executorService,
                new DefaultDegradationStrategy(1L, 1L, passRate,new Class[]{FileNotFoundException.class}, errorObject,
                        FailurePriority.ERROR_OBJECT, FastFail.TRUE,false));

        assertEquals(errorObject, handler.invoke(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return stub.callService();
            }
        }));
    }

    private long runServiceUnderLoad(long timestamp,
                                     int concurrentLoad,
                                     int capacity,
                                     DefaultDegradationStrategy degradationStrategy)
            throws Exception {
        //set up execution pool
        final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(capacity);
        //set up degradation handler with execution pool and config
        final DegradationHandler handler = new DegradationHandler(executorService, degradationStrategy);

        //set up concurrent load provider
        ExecutorService loadProvider = Executors.newFixedThreadPool(concurrentLoad);
        //Queue concurrent calls in a list for addition to load provider
        List<DegradationHandler> handlerCalls = new ArrayList<DegradationHandler>();
        for (int i = 0; i < concurrentLoad; i++) {
            handlerCalls.add(handler);
        }
        //add list of embedded calls to provider and get futures.   basically service.callService() wrapped in callable
        //  only for unit testing purposes
        final StubbedService stub = new StubbedServiceImpl();
        final List<Future<Integer>> futures = setUpLoadingThreads(loadProvider, handlerCalls, new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                return stub.callService();
            }
        });
        for (Future future : futures) {
            try {
                assertEquals(0, future.get());
            } catch (ExecutionException e) {
                //degradation handler may throw a RuntimeException
                throw (Exception) e.getCause();
            } catch (Exception e) {
                throw e;
            }
        }
        //check total execution for concurrent execution
        loadProvider.shutdown();
        return System.currentTimeMillis() - timestamp;
    }

    private List<Future<Integer>> setUpLoadingThreads(final ExecutorService loadProvider,
                                                      final List<DegradationHandler> handlerCalls,
                                                      final TargetCallback targetCallback) {

        //submit callables and assign futures
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        for (final DegradationHandler handler : handlerCalls) {
            futures.add(loadProvider.submit(new Callable<Integer>() {
                public Integer call() throws Exception {
                    try {
                        return (Integer) handler.invoke(targetCallback);
                    } catch (Throwable e) {
                        if (e instanceof Exception)
                        {
                            throw (Exception) e;
                        }
                        else
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            })
            );
        }
        return futures;
    }
}
