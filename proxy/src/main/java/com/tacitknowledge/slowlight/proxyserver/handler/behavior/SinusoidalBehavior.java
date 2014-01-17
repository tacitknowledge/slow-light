package com.tacitknowledge.slowlight.proxyserver.handler.behavior;

import java.util.Map;

/**
 * Sinusoidal function implementation, values of this function could be controlled by passing amplitude and period parameters.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class SinusoidalBehavior extends BehaviorFunction
{

    private static final String ARG_PERIOD = "period";
    private static final String ARG_AMPLITUDE = "amplitude";

    @Override
    public Object evaluate(final Map<String, ?> params)
    {
        long period = Long.parseLong((String) params.get(ARG_PERIOD));
        long amplitude = Long.parseLong((String) params.get(ARG_AMPLITUDE));

        long time = System.currentTimeMillis();

        return Long.toString((long) (amplitude * Math.sin(time / period) + amplitude));
    }
}
