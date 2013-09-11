package com.tacitknowledge.slowlight.embedded;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * User: witherspore
 * Date: 6/19/13
 * Time: 7:59 AM
 * Convenience class for proxying objects
 * <p/>
 * Useful in Spring files or code for wrapping services
 */
public class ProxyFactory {
    public static Object proxy(Object obj, InvocationHandler handler) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                getInterfaces(obj),
                handler
        );
    }

    static private Class[] getInterfaces(Object stub) {
        return stub.getClass().getInterfaces();

    }

    public Object createProxy(Object obj, InvocationHandler handler) {
        return proxy(obj, handler);
    }
}
