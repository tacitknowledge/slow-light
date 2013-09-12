package com.tacitknowledge.performance;

import java.util.List;

import com.tacitknowledge.performance.scenario.ScenarioSelector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Server
{
    private final int port;
    private final List<Scenario> scenarios;
    private final ScenarioSelector scenarioSelector;

    public Server(final int port, final List<Scenario> scenarios, final ScenarioSelector scenarioSelector)
    {
        this.port = port;
        this.scenarios = scenarios;
        this.scenarioSelector = scenarioSelector;
    }

    public int getPort()
    {
        return port;
    }

    public List<Scenario> getScenarios()
    {
        return scenarios;
    }

    public ScenarioSelector getScenarioSelector()
    {
        return scenarioSelector;
    }
}
