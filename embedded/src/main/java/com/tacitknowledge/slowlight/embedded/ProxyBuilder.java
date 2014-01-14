package com.tacitknowledge.slowlight.embedded;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

/**
 * A builder that creates proxies based on provided target concrete class and
 * {@link MethodHandler} instance.
 *
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
public class ProxyBuilder
{
    private Class aClass;
    private MethodHandler handler;

    public <T> ProxyBuilder aClass(final Class<T> aClass)
    {
        this.aClass = aClass;
        return this;
    }

    public ProxyBuilder handler(final MethodHandler handler)
    {
        this.handler = handler;
        return this;
    }

    public <T> T build() throws Exception
    {
        final ProxyFactory factory = new ProxyFactory();

        factory.setSuperclass(aClass);
        Class subClass = factory.createClass();

        Object proxy = subClass.newInstance();
        ((ProxyObject) proxy).setHandler(handler);

        return (T) proxy;
    }
}
