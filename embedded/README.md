#Slow Light Embedded

Slow Light Embedded is a sister tool to [Slow Light Proxy Server](../proxy).

# Where do I get Slow Light Embedded?
-------------------------
Slow Light Embedded is open source and is hosted at
[Github](http://github.com/tacitknowledge/slow-light).

You can include Slow Light Embedded in your Maven project via:

    <dependency>
      <groupId>com.tacitknowledge</groupId>
      <artifactId>slowlight</artifactId>
      <version>1.0.1</version>
    </dependency>


# Use it!

The DegradationHandlerIntegrationTest shows a number of different modes, but in general you just need to do these things:
```java
 //This object needs to have an interface to proxy, but can be real app code, a real service, or a stub
 //  supporting concrete classes without interfaces is a future TODO
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
 //Set up a NamedThreadFactory. optional but highly suggested to make debugging and monitoring easier.
 //The below code would generate threads with names similar to the below
 // Thread[threadfactory-<factoryname>-groupnumber-1-thread-1],5,parentgroup-main-groupname-<factoryname>1]
 //Spring users may wish to use CustomizableThreadFactory, which supports naming
 ThreadFactory threadFactory = new NamedThreadFactory("<factoryname>");
 //set up a fixed thread pool
 ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(capacity,threadFactory);
 //set up InvocationHandler with execution pool and strategy
 final DegradationHandler handler = new DegradationHandler(conversation,
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
```java
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
```java
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
```java
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
```java
// set up a base 500 ms response.  Note: it will be randomized to something between 75% and 125% of the service demand
long serviceDemandTime = 500L;
//When service timeout is greater than response time, responses will be delayed by a general scaling function
// of Math.exp(pool utilization).  This scaling function maxes at around the serviceTimeout value
long serviceTimeout = 1500L;
// 90% pass rate.  Roughly, this means that when the adjusted response time is over 93% of the service timeout
// the system will throw an Exception randomly selected from an array
double passRate = 0.9;
//Exceptions to throw.  Will try and use the new Exception(String s) constructor, then fall back on default.
//    don't add any checked exceptions which aren't part of the API on your target object.
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
```java
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


