Slow Light [![Build Status](https://secure.travis-ci.org/tacitknowledge/slow-light.png?branch=master)](http://travis-ci.org/tacitknowledge/slow-light)
==========

In 1999, Danish physicist Lene Vestergaard Hau led a combined team from Harvard University and the Rowland Institute
for Science which succeeded in slowing a beam of light to about 17 meters per second.

Slow Light degrades response times of object methods and synchronous remote calls as concurrency increases.

We've used it primarily to test monitoring and fault tolerance of integration points under scalability tests.  We love
using it to test [Hystrix](https://github.com/Netflix/Hystrix) integrations and configurations.

# Introduction and Motivation

Slow Light consists of two sibling tools, Slow Light Embedded and Slow Light Proxy. Both tools interpose themselves
between a caller and an API to degrade responses, either with response times or error creation.

_Why do this?_ We've found that certain points in system architectures will inevitably experience degradation and
failure.  For instance, an application may leverage a remote address validation service.  That address validation
service, at some point in the future, will probably have an outage or scaling issues or just the network will fail.
Slow Light provides us with the ability to examine the client application's behavior under a variety of scenarios
where a remote service is having issues.  Typically, this is done during performance testing; it often exposes
situations where calls results can be cached better or logic can be made fault tolerant.  We've even seen situations
where degraded responses with a third party caused out-of-memory errors in the JVM because objects could not be released
to garbage collection frequently enough under load.

When Slow Light is combined with specialized Fault Tolerance tools such as [Hystrix](https://github.com/Netflix/Hystrix),
Slow Light becomes similar to testing smoke alarms and automatic sprinkler systems.  Slow Light creates the smoke which
triggers fault tolerance code and sets of alarms.

# Slow Light Architectures

**Slow Light Embedded**
Slow Light Embedded runs inside a Java Process and wraps service interfaces with a Java Proxy that incorporates a
ThreadPool and specialized InvocationHandler; the handler monitors concurrency on the ThreadPool and degrades Proxy
responses according to configuration and concurrency rules.

![alt text](https://raw.github.com/tacitknowledge/slow-light/development/images/SlowLightEmbedded.png "Embedded Architecture")

Slow Light Embedded requires altering code within your application or IoC configuration.  This is a fairly simple
process when using Spring, Guice, PicoContainer, or other IoC injectors that support an AOP model.  When not using one
of these tools, Slow Light Embedded is usually done at service instantiation - often using existing factories.  Many
people provide toggling that enables or disables Slow Light Embedded.

_with Hystrix_
With a Fault Tolerance tool like Hystrix, which also proxies/wraps Service APIs, we insert Hystrix around Slow Light.
Slow Light itself wraps

![alt text](https://raw.github.com/tacitknowledge/slow-light/development/images/SlowLightEmbeddedWithHystrix.png "Embedded With Hystrix")

**Slow Light Proxy**
Slow Light Proxy is a standalone JVM application that proxies remote, synchronous TCP/IP calls.  It does not require
altering code in client applications as it runs external to the process.  Slow Light Proxy uses Netty and some special
ChannelHandler implementations to slow, delay, discard, forward, and generally play mischevious games with remote
calls.

![alt text](https://raw.github.com/tacitknowledge/slow-light/development/images/SlowLightProxy.png "Proxy Architecture")


_with Hystrix_

When using Slow Light Proxy with a Fault Tolerance tool like Hystrix, you can achieve the same smoke and smoke alarm
behavior as Slow Light Embedded.

![alt text](https://raw.github.com/tacitknowledge/slow-light/development/images/SlowLightProxyWithHystrix.png "Embedded With Hystrix")

A disadvantage of Slow Light Proxy is that it can't create faults and degradation with system resources like file I/O.
If you need to simulate failures in non-network resources, use Slow Light Embedded.



# Dependencies
Just Java, no third party libraries outside the unit testing frameworks.

# Where do I get Slow Light?
-------------------------
Slow Light is open source and is hosted at
[Github](http://github.com/tacitknowledge/slow-light).

You can include Slow Light in your Maven project via:

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

# Future Feature Notes

**stable releases**

Currently the _master_ branch and release version slowlight-1.0.1 reflects the embedded framework capabilities only.
 It is available in the public maven repository as com.tacitknowledge:slowlight:1.0.1
```
     <dependency>
       <groupId>com.tacitknowledge</groupId>
       <artifactId>slowlight</artifactId>
       <version>1.0.1</version>
     </dependency>
```
**unproven stuff - use at your own risk**

Slowlight-Proxy is available in the _development_ branch.  You may need to modify code for your needs.

Feature development for embedded mode on concrete objects is also in the development branch.

Neither are ready for public release

# Release Notes

Slow Light development progresses against the _development_ branch, with merges into master at releases.

Currently Slow Light only supports embedded mode in its first release. Slow Light Proxy is on track for the second
release. If you are feeling lucky, you can grab the Slow Light Proxy code in the development branch.



