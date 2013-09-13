package com.tacitknowledge.slowlight.proxyserver.scenario;

import java.util.List;

import com.tacitknowledge.slowlight.proxyserver.Scenario;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ScenarioSelector
{
    Scenario select(List<Scenario> scenarios);
}

