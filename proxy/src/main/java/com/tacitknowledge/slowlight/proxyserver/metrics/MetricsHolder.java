package com.tacitknowledge.slowlight.proxyserver.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class MetricsHolder
{
    @Monitor(name = "counter", type = DataSourceType.COUNTER)
    public final AtomicInteger counter = new AtomicInteger(0);

    @Monitor(name="time", type = DataSourceType.COUNTER)
    public final AtomicLong time = new AtomicLong(0);

    @Monitor(name="bytesIn", type = DataSourceType.COUNTER)
    public final AtomicLong bytesIn = new AtomicLong(0);

    @Monitor(name="bytesOut", type = DataSourceType.COUNTER)
    public final AtomicLong bytesOut = new AtomicLong(0);

}
