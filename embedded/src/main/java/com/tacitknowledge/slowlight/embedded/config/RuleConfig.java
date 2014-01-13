package com.tacitknowledge.slowlight.embedded.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class RuleConfig
{
    private long serviceDemandTime;
    private long serviceTimeout;
    private double passRate;
    private int threads;

    private Map<String, List<String>> applyTo = new HashMap<String, List<String>>();

    public long getServiceDemandTime()
    {
        return serviceDemandTime;
    }

    public void setServiceDemandTime(final long serviceDemandTime)
    {
        this.serviceDemandTime = serviceDemandTime;
    }

    public long getServiceTimeout()
    {
        return serviceTimeout;
    }

    public void setServiceTimeout(final long serviceTimeout)
    {
        this.serviceTimeout = serviceTimeout;
    }

    public double getPassRate()
    {
        return passRate;
    }

    public void setPassRate(final double passRate)
    {
        this.passRate = passRate;
    }

    public int getThreads()
    {
        return threads;
    }

    public void setThreads(final int threads)
    {
        this.threads = threads;
    }

    public Map<String, List<String>> getApplyTo()
    {
        return applyTo;
    }

    public void setApplyTo(final Map<String, List<String>> applyTo)
    {
        this.applyTo = applyTo;
    }
}
