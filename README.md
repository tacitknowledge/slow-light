# Introduction and Motivation

Allows one to proxy an interface with an InvocationHandler, the DegradationHandler, that provides a thread pool
which constrains and slows throughput.  Also can generate error responses and exceptions as pool utilization increases.

Originally, something similar was used on Motorola MRC to degrade response times on integration points and introduce
errors.  Newly updated, it includes more flexible support for failure modes, checked exception handling, and
general degradation configuration.

# Dependencies
Just Java, no third party libraries outside the test classes.

# Use it!

The ClientDriverTest shows a number of different modes, but in general you just need to do these things:
```

 Object targetToWrapInProxy;
 .
 .
 .
 //set up how the service should degrade
 DegradationStrategy degradationStrategy
        = new DefaultDegradationStrategy(serviceDemandTime,
                serviceTimeout,
                passRate,
                new Class<Exception>[]{MyException.class}
          );
 //set up thread pool
 ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(capacity);
 //set up InvocationHandler with execution pool and strategy
 final DegradationHandler handler = new DegradationHandler(stub,
                executorService, degradationStrategy);
 Object wrappedProxy = Proxy.newProxyInstance(targetToWrapInProxy.getClass().getClassLoader(),
                targetToWrapInProxy.getClass().getInterfaces(),
                handler);
 //now replace calls to target with the wrapped proxy.  In general, pretty simple to integrate with IoC frameworks,
 //                  AOP stuff, or JNDI
```

# Some Sample Configurations
Most everything is driven through the DefaultDegradationStrategy.

Setting up a pass through configuration with no degradation.

While pool size is constrained,  service calls immediately
execute, response times are never delayed, and no errors are created or Exceptions thrown.
```
long serviceDemandTime = 0L;
long serviceTimeout = 0L;
double passRate = 1.0;
DegradationStrategy degradationStrategy
        = new DefaultDegradationStrategy(serviceDemandTime,
                serviceTimeout,
                passRate
          );
```
Setting up a roughly constant delay on response times.
```
// set up a base 500 ms response.  Note: it will be randomized to something between 75% and 125% of the service demand
long serviceDemandTime = 500L;
//When timeout matches demand time, response times do not increase as utilization increases
long serviceTimeout = 500L;
double passRate = 1.0; // 100% pass rate so no errors occur
DegradationStrategy degradationStrategy
        = new DefaultDegradationStrategy(serviceDemandTime,
                serviceTimeout,
                passRate
          );
```

Setting up a a degradation curve.
```
// set up a base 500 ms response.  Note: it will be randomized to something between 75% and 125% of the service demand
long serviceDemandTime = 500L;
//When service timeout is greater than response time, responses will be delayed by a general scaling function
// of Math.exp(pool utilization).  This scaling function maxes at around the serviceTimeout value
long serviceTimeout = 1500L;
double passRate = 1.0; // 100% pass rate so no errors occur
DegradationStrategy degradationStrategy
        = new DefaultDegradationStrategy(serviceDemandTime,
                serviceTimeout,
                passRate
          );
```

Setting up a a degradation curve and throwing errors
```
// set up a base 500 ms response.  Note: it will be randomized to something between 75% and 125% of the service demand
long serviceDemandTime = 500L;
//When service timeout is greater than response time, responses will be delayed by a general scaling function
// of Math.exp(pool utilization).  This scaling function maxes at around the serviceTimeout value
long serviceTimeout = 1500L;
// 90% pass rate.  Roughly, this means that when the adjusted response time is over 93% of the service timeout
// the system will throw an Exception randomly selected from an array
double passRate = 0.9;
//Exceptions to throw.  Will try and use the new Exception(String s) constructor, then fall back on default.
Class exceptions = new Class[]{MyException.class}

DegradationStrategy degradationStrategy
        = new DefaultDegradationStrategy(serviceDemandTime,
                serviceTimeout,
                passRate,
                exceptions
          );
```

Setting up a a degradation curve and returning an error object
You may want to do this if the interface short circuits errors and returns a default object, such as something
representing "Service not available.  Please try later".  Not really an exception, but a valid and not helpful response.
```
long serviceDemandTime = 500L;
long serviceTimeout = 1500L;
double passRate = 0.9;
//can be any Object.  Here, just using a non-zero int
final Integer errorObject = new Integer(25);
DegradationStrategy = new DefaultDegradationStrategy(serviceDemandTime,
        serviceTimeout,
        //again, 90%.
        passRate,
        new Class[]{FileNotFoundException.class},
        //here's the errorObject we want to return when the 10% failures occur
        errorObject,
        //setting this priority causes error objects to be returned rather than Exceptions thrown
        FailurePriority.ERROR_OBJECT,
        //FastFail true causes the response to immediately return rather than be delayed
        FastFail.TRUE,
        //this boolean will cause pool items to timeout if set to true.
        //  basically future.get(serviceTimeout,TimeUnit.MILLIS)
        false
    );

```

# Notes
The default implementations are thread safe.  You can re-use a single proxy across threads, assuming the target object
is also safe.

# Extension Points
Feel free to write your own DegradationStrategy and pop it into the DegradationHandler
