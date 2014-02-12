#Slow Light Embedded

Slow Light Embedded is a sister tool to [Slow Light Proxy Server](../proxy).

# Where do I get Slow Light Embedded?
-------------------------
Slow Light Embedded is open source and is hosted at
[Github](http://github.com/tacitknowledge/slow-light).

You can include Slow Light Embedded in your project via:

    Maven dependency:

        <dependency>
          <groupId>com.tacitknowledge</groupId>
          <artifactId>slowlight</artifactId>
          <version>1.0.1</version>
        </dependency>

    Ivy dependency:

        <dependency org="com.tacitknowledge" name="slowlight" rev="1.0.1" />

    Grapes dependency:

        @Grapes(
            @Grab(group='com.tacitknowledge', module='slowlight', version='1.0.1')
        )

    Gradle dependency:

        'com.tacitknowledge:slowlight:1.0.1'

# Use it!

Slow Light Embedded supports two different usage approaches - programatic and declarative.

## 1. Programmatic Mode

The DegradationHandlerIntegrationTest shows a number of different modes, but in general you just need to do these things:
```java
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
 final DegradationHandler degradationHandler = new DegradationHandler(conversation,
                executorService, degradationStrategy);
 final DegradationProxyHandler proxyHandler = new DegradationProxyHandler(targetToWrapInProxy, degradationHandler);
 Object wrappedProxy = new ProxyBuilder().aClass(Object.class).handler(proxyHandler).build();
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

## 2. Declarative Mode

The benefit of declarative mode is that it doesn't requrie code changes on the target (under test) system. For declarative configuration and usage Slow Light Embedded uses AspectJ and JSON, and here is what needs to be done
to start using it in this mode:

### a. classpath configuration
Add slowlight-embedded.jar to the target (under test) system classpath.

### b. config folder
In the same folder where the slowlight-embedded.jar is located create a folder called config with the following structure:
       
    slowlight-embedded.jar
    config
      |--slowlight-embedded.config
      |--META-INF
         |--aop.xml
             
**aop.xml** tells AspectJ where to apply aspects and should be configured similar to this example:
```xml
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "http://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>
    <weaver>
        <include within="com.project.target.package.TargetClass" />
        <include within="com.tacitknowledge.slowlight.embedded.aspect.*" />
    </weaver>
    <aspects>
        <aspect name="com.tacitknowledge.slowlight.embedded.aspect.DegradationAdvice" />
    </aspects>
</aspectj>
```
       
Where for example com.project.target.package.TargetClass is the fully qualified name of the class
you want "degradation behaviour" to be applied, instead of specifing a concrete class you can specify
a whole package example com.project.target.package.*. Anyway since this is a pure AspectJ config
you can find detailed information in AspectJ documentation.

__Note:__ it may seem strange but please make sure that __com.tacitknowledge.slowlight.embedded.aspect.*__ package is declared
in __weaver__ section as well, otherwise it will throw a NoSuchMethodError at runtime. This is related to an AspectJ defect,
for more details please refer to https://bugs.eclipse.org/bugs/show_bug.cgi?id=362411.

**slowlight-embedded.config** stands for degradation rules configuration and could be seen
as a mapping between degradation rule and class/methods, or in other words to what classes 
and methods a degradation rule should be applied.
   
slowlight-embedded.config example:
    
```json
{
    "rules" : [
        {
            "serviceDemandTime" : "100",
            "serviceTimeout" : "2000",
            "passRate" : "80",
            "threads" : 16,
            "randomExceptions" : ["java.lang.Exception","java.rmi.AccessException"],

            "applyTo" : {
                "com.project.target.package.SomeClass1" : ["method1", "method2"],
                "com.project.target.package.SomeClass2" :["method3"]
            }
        },
        {
            "serviceDemandTime" : "500",
            "serviceTimeout" : "10000",
            "passRate" : "50",
            "threads" : 16,
            "randomExceptions" : [java.lang.Exception, "java.rmi.RemoteException"],

            "applyTo" : {
                "com.project.target.package.SomeClass3" : ["method"]
            }
        }
    ]
}
```
    
The meaning of degradation rules parameters (e.g. serviceDemandTime, passRate, etc.) are similar
to what is described in programatic mode (see above).

# Notes
The default implementations are thread safe.  You can re-use a single proxy across threads, assuming the target object
is also safe.

# Extension Points
Feel free to write your own DegradationStrategy and pop it into the DegradationHandler


