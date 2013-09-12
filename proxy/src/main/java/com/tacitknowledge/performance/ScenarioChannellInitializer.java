package com.tacitknowledge.performance;

import java.util.List;

import com.tacitknowledge.performance.metrics.MetricsHandler;
import com.tacitknowledge.performance.scenario.ScenarioSelector;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ScenarioChannellInitializer extends ChannelInitializer<SocketChannel>
{
    private final List<Scenario> scenarios;
    private final ScenarioSelector scenarioSelector;

    public ScenarioChannellInitializer(final List<Scenario> scenarios, final ScenarioSelector scenarioSelector)
    {
        this.scenarios = scenarios;
        this.scenarioSelector = scenarioSelector;
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception
    {
        Scenario scenario = scenarioSelector.select(scenarios);
        ch.config().setKeepAlive(false);
        ch.pipeline().addFirst(new MetricsHandler(scenario.getMetrics()));
        for (Component component : scenario.getComponents())
        {
            component.initChannel(ch);
        }
    }

}
