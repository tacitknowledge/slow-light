package com.tacitknowledge.perf.degradation.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
* Created by IntelliJ IDEA.
* User: mshort
* Date: 6/19/13
* Time: 7:59 AM
* To change this template use File | Settings | File Templates.
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
}
