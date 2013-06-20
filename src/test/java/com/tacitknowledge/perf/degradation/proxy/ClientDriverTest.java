package com.tacitknowledge.perf.degradation.proxy;

import static org.junit.Assert.*;

import com.tacitknowledge.perf.degradation.proxy.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/18/13
 * Time: 8:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClientDriverTest {

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
        assertTrue("Should have been less than 626 ms, but was " + totalTime, totalTime < serviceDemandTime * 1.3);

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
        DefaultDegradationStrategy degradationStrategy = new DefaultDegradationStrategy(serviceDemandTime, serviceTimeout, passRate,
                new Class[]{RuntimeException.class}, null, FailurePriority.EXCEPTION, FastFail.FALSE, true);
        runServiceUnderLoad(timestamp, concurrentLoad, capacity, degradationStrategy);
    }


    @Test(expected = RuntimeException.class)
    public void testBubbleRuntimeExceptionThroughThreadPoolAndProxy()
            throws Exception {
        StubbedService stub = new StubbedServiceImpl() {
            @Override
            public Integer callService() {
                throw new RuntimeException("trial exception");
            }
        };
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        //Wrap service with degradation handler
        final double passRate = 0.9;
        StubbedService proxy = (StubbedService) ProxyFactory.proxy(stub, new DegradationHandler(stub,
                executorService, new DefaultDegradationStrategy(1L, 1L, passRate)));
        proxy.callService();
    }

    @Test(expected = ConnectException.class )
    public void testBubbleConnectExceptionThroughThreadPoolAndProxy()
            throws Exception {
        StubbedService stub = new StubbedServiceErrorImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        final double passRate = 1.0;
        StubbedService proxy = (StubbedService) ProxyFactory.proxy(stub, new DegradationHandler(stub,
                executorService, new DefaultDegradationStrategy(1L, 1L, passRate)));
        proxy.callService();
    }
    @Test(expected = FileNotFoundException.class )
    public void testForceFileNotFoundExceptionFromClassArray()
            throws Exception {
        StubbedService stub = new StubbedServiceImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        final double passRate = 0.0;
        StubbedService proxy = (StubbedService) ProxyFactory.proxy(stub, new DegradationHandler(stub,
                executorService, new DefaultDegradationStrategy(1L, 1L, passRate,new Class[]{FileNotFoundException.class})));
        proxy.callService();
    }

    @Test(expected = UndeclaredThrowableException.class )
    public void testUndeclaredCheckedExceptionInExceptionClassArray()
            throws Exception {
        StubbedService stub = new StubbedServiceImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        final double passRate = 0.0;
        StubbedService proxy = (StubbedService) ProxyFactory.proxy(stub, new DegradationHandler(stub,
                executorService, new DefaultDegradationStrategy(1L, 1L, passRate,new Class[]{IOException.class})));
        proxy.callService();
    }

    @Test
    public void testReturnErrorObject()
            throws Exception {
        StubbedService stub = new StubbedServiceImpl();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        final double passRate = 0.0;
        final Integer errorObject = new Integer(25);
        StubbedService proxy = (StubbedService) ProxyFactory.proxy(stub, new DegradationHandler(stub,
                executorService, new DefaultDegradationStrategy(1L, 1L, passRate,new Class[]{FileNotFoundException.class},
                errorObject, FailurePriority.ERROR_OBJECT, FastFail.TRUE,false)));
        assertEquals(errorObject, proxy.callService());
    }

    private long runServiceUnderLoad(long timestamp,
                                     int concurrentLoad,
                                     int capacity,
                                     DefaultDegradationStrategy degradationStrategy)
            throws Exception {
        StubbedService stub = new StubbedServiceImpl();
        //set up execution pool
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(capacity);
        //set up degradation handler with execution pool and config
        final DegradationHandler handler = new DegradationHandler(stub,
                executorService, degradationStrategy);

        //set up proxy with handler
        final StubbedService proxy = (StubbedService) ProxyFactory.proxy(stub, handler);

        //set up concurrent load provider
        ExecutorService loadProvider = Executors.newFixedThreadPool(concurrentLoad);
        //Queue concurrent calls in a list for addition to load provider
        List<StubbedService> serviceCalls = new ArrayList<StubbedService>();
        for (int i = 0; i < concurrentLoad; i++) {
            serviceCalls.add(proxy);
        }
        //add list of proxy calls to provider and get futures.   basically service.callService() wrapped in callable
        //  only for unit testing purposes
        List<Future<Integer>> futures = setUpLoadingThreads(loadProvider, serviceCalls);
        for (Future future : futures) {
            try {
                assertEquals(new Integer(0), future.get());
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

    private List<Future<Integer>> setUpLoadingThreads(ExecutorService loadProvider, List<StubbedService> serviceCalls) {

        //submit callables and assign futures
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
        for (final StubbedService proxiedService : serviceCalls) {
            futures.add(loadProvider.submit(new Callable<Integer>() {
                public Integer call() throws Exception {
                    try {
                        return proxiedService.callService();
                    } catch (UndeclaredThrowableException e) {
                        if (e.getUndeclaredThrowable().getCause() != null)
                            throw (Exception) e.getUndeclaredThrowable().getCause();
                        throw (Exception) e.getUndeclaredThrowable();
                    }
                }
            })
            );
        }
        return futures;
    }


    public static interface StubbedService {
        Integer callService() throws ConnectException, FileNotFoundException;


    }

    public static class StubbedServiceErrorImpl implements StubbedService {
        public Integer callService() throws ConnectException, FileNotFoundException {
            throw new ConnectException();
        }

    }
    public static class StubbedServiceImpl implements StubbedService{
        public Integer callService() throws ConnectException, FileNotFoundException {
            return 0;
        }

    }


}
