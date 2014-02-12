package com.tacitknowledge.slowlight.embedded;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class to be used for thread locals manipulations (ex. copy thread locals value from parent to child thread),
 * such thread locals operations are required because slowlight embedded uses threads under the hood and it might cause
 * issues if application under tests relies on thread locals.
 *
 * Please note that this is not the safest implementation, but it seems to be the most appropriate solution (at the moment)
 * to workaround thread locals issue.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class ThreadLocalUtil
{

    private static final String CLASS_JAVA_LANG_THREAD_LOCAL$_THREAD_LOCAL_MAP = "java.lang.ThreadLocal$ThreadLocalMap";

    private static final String FIELD_THREAD_LOCALS = "threadLocals";
    private static final String FILED_THREAD_LOCAL_MAP_TABLE = "table";
    private static final String FIELD_THREAD_LOCAL_TABLE_VALUE = "value";

    private static final String METHOD_THREAD_LOCAL_MAP_SET = "set";
    private static final String METHOD_THREAD_LOCAL_MAP_GET = "get";

    /**
     * Copies thread locals from given source thread to the current thread.
     *
     * @param sourceThread thread locals source thread
     * @throws Exception if any thread locals access problems
     */
    public void propagateThreadLocals(final Thread sourceThread) throws Exception
    {
        if (sourceThread == Thread.currentThread())
        {
            return;
        }

        final Object parentThreadLocals = getThreadLocals(sourceThread);
        if (parentThreadLocals == null)
        {
            return;
        }

        initializeThreadLocals();
        final Object currentThreadLocals = getThreadLocals(Thread.currentThread());

        Object parentTable = getThreadLocalTable(parentThreadLocals);
        int threadLocalCount = Array.getLength(parentTable);
        for (int i = 0; i < threadLocalCount; i++)
        {
            final Object key = getThreadLocalKey(parentTable, i);
            final Object value = getThreadLocalValue(parentTable, i);

            if (key != null && value != null)
            {
                setThreadLocalValue(currentThreadLocals, key, value);
            }
        }
    }

    private void initializeThreadLocals()
    {
        new ThreadLocal().get();
    }

    private void setThreadLocalValue(final Object currentThreadLocals, final Object key, final Object value) throws Exception
    {
        final Class<?> threadLocalMapClass = Class.forName(CLASS_JAVA_LANG_THREAD_LOCAL$_THREAD_LOCAL_MAP);
        final Method setMethod = threadLocalMapClass.getDeclaredMethod(METHOD_THREAD_LOCAL_MAP_SET, ThreadLocal.class, Object.class);
        setMethod.setAccessible(true);
        setMethod.invoke(currentThreadLocals, key, value);
    }

    private Object getThreadLocals(final Thread thread) throws Exception
    {
        Field threadLocalsField = Thread.class.getDeclaredField(FIELD_THREAD_LOCALS);
        threadLocalsField.setAccessible(true);

        return threadLocalsField.get(thread);
    }

    private Object getThreadLocalTable(final Object threadLocals) throws Exception
    {
        Class threadLocalMapKlazz = Class.forName(CLASS_JAVA_LANG_THREAD_LOCAL$_THREAD_LOCAL_MAP);
        Field tableField = threadLocalMapKlazz.getDeclaredField(FILED_THREAD_LOCAL_MAP_TABLE);
        tableField.setAccessible(true);

        return tableField.get(threadLocals);
    }

    private Object getThreadLocalKey(final Object table, final int i) throws Exception
    {
        Object key = null;

        Object entry = Array.get(table, i);
        if (entry != null)
        {
            Method getMethod = Reference.class.getDeclaredMethod(METHOD_THREAD_LOCAL_MAP_GET);
            key = getMethod.invoke(entry);
        }

        return key;
    }

    private Object getThreadLocalValue(final Object table, final int i) throws Exception
    {
        Object value = null;

        Object entry = Array.get(table, i);
        if (entry != null)
        {
            Field valueField = entry.getClass().getDeclaredField(FIELD_THREAD_LOCAL_TABLE_VALUE);
            valueField.setAccessible(true);
            value = valueField.get(entry);
        }

        return value;
    }
}
