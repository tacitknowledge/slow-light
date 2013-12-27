package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a parametrized configuration model.
 * Whenever you need to parametrize a particular chunk of configuration you can extend from this class what will add a <b>params</b>
 * property to you configuration, so later those parameters could be used for ex. for server, handler, etc. configuration.<br/>
 *
 * <br/>
 * <b>An example of parametrized configuration (JSON), please note params property for various config elements<b/>
 * <pre>
 * {@code
 * ...
 *      {
 *          "id" : "testServer2",
 *          ...
 *          "params" : {"host" : "localhost", "port" : "8080"},
 *          "handlers" : [
 *              {
 *                  "name" : "delayHandler",
 *                  ...
 *                  "params" : {"maxDataSize" : "0", "delay" : "500"}
 *              }
 *          ]
 *      }
 * }
 * </pre>
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ParametrizedConfig
{
    private Map<String, String> params = new HashMap<String, String>();

    public void setParams(final Map<String, String> params)
    {
        this.params = params;
    }

    public Map<String, String> getParams()
    {
        return params;
    }

    public String getParam(final String key)
    {
        return getParam(key, true);
    }

    public String getParam(final String key, final boolean required)
    {
        final String param = params.get(key);

        if (required && param == null)
        {
            throw new IllegalArgumentException("Config parameter [" + key + "] doesn't exists");
        }

        return param;
    }
}
