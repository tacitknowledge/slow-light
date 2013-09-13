package com.tacitknowledge.slowlight.proxyserver.scenario;

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;

import com.tacitknowledge.slowlight.proxyserver.Scenario;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class RandomScenarioSelector implements ScenarioSelector
{
    @Override
    public Scenario select(final List<Scenario> scenarios)
    {
        int ix = RandomUtils.nextInt(scenarios.size());
        return scenarios.get(ix);
    }
}
