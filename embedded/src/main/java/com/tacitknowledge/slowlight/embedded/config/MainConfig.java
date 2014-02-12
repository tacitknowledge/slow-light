package com.tacitknowledge.slowlight.embedded.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class MainConfig
{
    private List<RuleConfig> rules = new ArrayList<RuleConfig>();

    public List<RuleConfig> getRules()
    {
        return rules;
    }

    public void setRules(final List<RuleConfig> rules)
    {
        this.rules = rules;
    }
}
