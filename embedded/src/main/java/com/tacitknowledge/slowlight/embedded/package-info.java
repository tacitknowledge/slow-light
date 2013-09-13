/**
 * The proxyserver degradation package provides a simple proxying api to cause delays and failures
 * when calling an object under a concurrency situation.
 *
 * In short, if you have a service and wrap it in this embedded, you can simulate increased latency or scalability
 * failures under concurrent load.
 *
 *
 * The DegradationHandler uses the strategy and a threadpool to submit Callables to the threadpool.  These
 * DegradationCallables put the thread pool threads to sleep for some amount of time, generally calculated based on
 * current thread pool utilization and the min/max latency of the DegradationStrategy.  If the sleep time exceeds a certain
 * threshold (pass rate), the calls eventually will be overriden with a configured error object or throw an exception to
 * simulate a failure.
 *
 * @see DegradationHandler
 * @see DegradationCallable
 * @see DegradationStrategy
 * @see DegradationPlan - a DTO like object containing results of applying specific strategy rules for a specific call
 *
 * The DegradationStrategy determines configs for min and max latency via service demand and timeout.  It also allows
 * configuration of errors, failure rates, and throwables.
 *
 * @see DefaultDegradationStrategy
 *
 */
package com.tacitknowledge.slowlight.embedded;
