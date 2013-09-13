package com.tacitknowledge.slowlight.proxyserver.scenario;

import java.util.List;

import com.tacitknowledge.slowlight.proxyserver.Scenario;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ProprotionalCountSelector implements ScenarioSelector
{
    @Override
    public Scenario select(final List<Scenario> scenarios)
    {
        if(scenarios.size()==1) {
            return scenarios.get(0);
        }

        double min = Double.MAX_VALUE;
        Scenario selected = null;
        for (Scenario scenario : scenarios)
        {
            if(scenario.getWeight() == 0) {
                continue;
            }
            double weighedCount = scenario.getMetrics().counter.get()/scenario.getWeight();

            if(weighedCount<min) {
                min = weighedCount;
                selected = scenario;
            }
        }

        return selected;
    }
}
