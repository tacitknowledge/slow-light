package com.tacitknowledge.slowlight.proxyserver;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.tacitknowledge.slowlight.proxyserver.metrics.MetricsHolder;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Scenario implements Serializable
{
    private final List<? extends Component> components;
    private transient volatile MetricsHolder metrics;
    private final double weight;

    public Scenario(double weight, final List<? extends Component> components)
    {
        Preconditions.checkArgument(weight>0);
        this.components = components;
        this.weight = weight;
    }

    /* Contructors */

    public Scenario(final List<Component> components) {
        this(1,components);
    }

    public Scenario(double weight, Component... components)
    {
        this(weight, Lists.newArrayList(components));
    }

    public Scenario(Component... components)
    {
        this(1, components);
    }

    public List<? extends Component> getComponents()
    {
        return components;
    }


    /* Getters */

    public MetricsHolder getMetrics()
    {
        if(metrics == null) {
            synchronized (this) {
                if(metrics == null) {
                    metrics = new MetricsHolder();
                }
            }
        }
        return metrics;
    }

    public double getWeight()
    {
        return weight;
    }
}
