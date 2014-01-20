package com.tacitknowledge.slowlight.proxyserver.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a behavior function configuration model. In order to
 * configure a behavior function specify the name of the handler parameter (ex.
 * writeLimit) as paramName property value, what will cause behavior function to
 * be attached and applied for that particular parameter, also specify the fully
 * qualified name of the class which will provide function implementation.
 * Please note that function class should implement the
 * {@link com.tacitknowledge.slowlight.proxyserver.handler.behavior.BehaviorFunction}
 * interface.<br/>
 *
 * <br/>
 * <b>An example of behavior function configuration (JSON), please note that
 * this function is attached to the handler delay param by its name<b/>
 *
 * <pre>
 * {@code
 * ...
 * "handlers" : [
 *      {
 *          "name" : "delayHandler",
 *          "type" : "com.tacitknowledge.slowlight.proxyserver.handler.DelayChannelHandler",
 *          "params" : {"maxDataSize" : "0", "delay" : "500"},
 *          "behaviorFunctions" : [
 *              {
 *                  "paramName" : "delay",
 *                  "type" : "com.tacitknowledge.slowlight.proxyserver.handler.behavior.SinusoidalBehavior",
 *                  "ranges" : {"5" : "10", "10" : "15"}
 *              }
 *          ]
 *      },
 *      ...
 * ]
 * ...
 * }
 * </pre>
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class BehaviorFunctionConfig extends ParameterizedConfig
{
    private String paramName;
    private String type;
	private String start;
	private String stop;

	private Map<String, String> ranges = new HashMap<String, String>();

    public String getParamName()
    {
        return paramName;
    }

    public void setParamName(final String paramName)
    {
        this.paramName = paramName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

	public Map<String, String> getRanges() {
		return ranges;
	}

	public void setRanges(Map<String, String> ranges) {
		this.ranges = ranges;
	}

	public String getId() {
		StringBuilder id = new StringBuilder(paramName);
		id.append("_").append(type);

		if (ranges == null) {
			return id.toString();
		}

		Iterator<String> keys = ranges.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			id.append("[").append(key).append(" - ").append(ranges.get(key))
			        .append("]");
		}

		return id.toString();
	}
}
