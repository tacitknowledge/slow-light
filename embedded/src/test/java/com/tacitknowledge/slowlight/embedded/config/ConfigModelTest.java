package com.tacitknowledge.slowlight.embedded.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

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
	public void testRuleConfigModel() throws ClassNotFoundException
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

		List<String> randomExceptions = new ArrayList<String>();
		randomExceptions.add("java.lang.Exception");
		randomExceptions.add("java.rmi.AccessException");
		ruleConfig.setRandomExceptions(randomExceptions);


        assertThat(ruleConfig.getServiceDemandTime(), is(equalTo(serviceDemandTime)));
        assertThat(ruleConfig.getServiceTimeout(), is(equalTo(serviceTimeout)));
        assertThat(ruleConfig.getPassRate(), is(equalTo(passRate)));
        assertThat(ruleConfig.getThreads(), is(equalTo(threads)));
        assertThat(ruleConfig.getApplyTo(), is(equalTo(applyTo)));
		assertThat(ruleConfig.getRandomExceptions(),
		        is(equalTo(randomExceptions)));
		assertEquals(2, ruleConfig.getRandomExceptionsAsClasses().size());
    }

	@Test
	public void testRuleConfigModelForNoExceptions()
	        throws ClassNotFoundException {
		RuleConfig ruleConfig = new RuleConfig();
		List<Class> exceptions = ruleConfig.getRandomExceptionsAsClasses();
		assertEquals(0, exceptions.size());
	}

	@Test
	public void testRuleConfigModelForEmptyExceptions()
	        throws ClassNotFoundException {
		RuleConfig ruleConfig = new RuleConfig();
		ruleConfig.setRandomExceptions(new ArrayList<String>());
		List<Class> exceptions = ruleConfig.getRandomExceptionsAsClasses();
		assertEquals(0, exceptions.size());
	}
}
