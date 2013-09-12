package com.tacitknowledge.performance.scenario;

import java.util.List;

import com.tacitknowledge.performance.Scenario;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ScenarioSelector
{
    Scenario select(List<Scenario> scenarios);
}

