package com.tacitknowledge.slowlight.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: witherspore
 * Date: 9/3/13
 * Time: 10:44 AM
 * <p/>
 * Some utility methods to avoid code duplication
 */
public class ProxyUtil {

    /**
     * Convenience method for directly invoking the target method without going through a Callable
     *
     * @param target
     * @param method
     * @param args
     * @return Object from target
     * @throws Exception
     */
    public Object invokeTarget(Object target, Method method, Object[] args) throws Exception {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

}
