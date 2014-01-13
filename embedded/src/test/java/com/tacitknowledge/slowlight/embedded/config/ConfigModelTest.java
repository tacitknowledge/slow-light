package com.tacitknowledge.slowlight.embedded.config;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ConfigModelTest
{
    @Test
    public void testMainConfigModel()
    {
        final RuleConfig ruleConfig = new RuleConfig();

        final MainConfig mainConfig = new MainConfig();
        mainConfig.setRules(Collections.singletonList(ruleConfig));

        assertThat(mainConfig.getRules().size(), is(equalTo(1)));
        assertThat(mainConfig.getRules().get(0), is(equalTo(ruleConfig)));
    }

    @Test
    public void testRuleConfigModel()
    {
        final long serviceDemandTime = 10L;
        final long serviceTimeout = 1000L;
        final double passRate = 80;
        final int threads = 16;

        final Map<String, List<String>> applyTo = new HashMap<String, List<String>>();
        applyTo.put("test.Class", Arrays.asList("method1", "method2"));

        final RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setServiceDemandTime(serviceDemandTime);
        ruleConfig.setServiceTimeout(serviceTimeout);
        ruleConfig.setPassRate(passRate);
        ruleConfig.setThreads(threads);
        ruleConfig.setApplyTo(applyTo);

        assertThat(ruleConfig.getServiceDemandTime(), is(equalTo(serviceDemandTime)));
        assertThat(ruleConfig.getServiceTimeout(), is(equalTo(serviceTimeout)));
        assertThat(ruleConfig.getPassRate(), is(equalTo(passRate)));
        assertThat(ruleConfig.getThreads(), is(equalTo(threads)));
        assertThat(ruleConfig.getApplyTo(), is(equalTo(applyTo)));
    }
}
