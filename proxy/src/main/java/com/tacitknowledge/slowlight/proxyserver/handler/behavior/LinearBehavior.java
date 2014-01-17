package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Linear function implementation, values of this function could be controlled by passing value parameter.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class LinearBehavior extends BehaviorFunction
{
    private static final Logger LOG = LoggerFactory.getLogger(LinearBehavior.class);

    private static final String ARG_VALUE = "value";

    @Override
    public Object evaluate(final Map<String, ?> params)
    {
        final Object value = params.get(ARG_VALUE);

        LOG.debug("apply linear value [{}]", value);

        return value;
    }
}
