package com.tacitknowledge.slowlight.proxyserver.handler;

import com.netflix.config.jmx.BaseConfigMBean;
import com.netflix.config.jmx.ConfigMBean;
import com.tacitknowledge.slowlight.proxyserver.config.HandlerConfig;
import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;

/**
 * Config manager provides methods to expose handler parameters via JMX as dynamic configuration,
 * this will allow someone to adjust parameters values at runtime when needed.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class HandlerConfigManager
{
    private static final Logger LOG = LoggerFactory.getLogger(HandlerConfigManager.class);

    /**
     * Registers specified handler configuration (parameters) as JMX MBean.
     *
     * @param handlerConfig handler configuration
     * @param config handler parameters
     * @return configuration MBean
     */
    public static ConfigMBean registerConfigMBean(final HandlerConfig handlerConfig, final AbstractConfiguration config)
    {
        StandardMBean mbean;
        ConfigMBean bean;

        try
        {
            MBeanServer mbs = getMBeanServer();

            bean = new BaseConfigMBean(config);
            mbean = new StandardMBean(bean, ConfigMBean.class);

            final ObjectName objectName = getJMXObjectName(handlerConfig);
            if (mbs.isRegistered(objectName))
            {
                LOG.warn("Skip Config MBean registration because there is one registered with the same name [{}]", objectName);
                return null;
            }

            mbs.registerMBean(mbean, objectName);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return bean;
    }

    protected static MBeanServer getMBeanServer()
    {
        return ManagementFactory.getPlatformMBeanServer();
    }

    protected static ObjectName getJMXObjectName(final HandlerConfig handlerConfig)
    {
        try
        {
            return new ObjectName("slowlight-config" + ":class=" + handlerConfig.getName());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
