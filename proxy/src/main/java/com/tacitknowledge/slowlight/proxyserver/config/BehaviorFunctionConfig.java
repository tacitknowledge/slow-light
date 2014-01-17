package com.tacitknowledge.slowlight.proxyserver.config;

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
 *                  "type" : "com.tacitknowledge.slowlight.proxyserver.handler.behavior.SinusoidalBehavior"
 *                  "start" : "5",
 *                  "stop" : "10"
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

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public String getId() {
		return paramName + "_" + type + "["
 + start + " - " + stop + "]";
	}
}
