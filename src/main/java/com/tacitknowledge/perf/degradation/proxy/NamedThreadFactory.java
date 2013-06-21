package com.tacitknowledge.perf.degradation.proxy;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/21/13
 * Time: 9:50 AM
 *
 * If you are using Spring, consider the CustomizableThreadFactory instead
 *
 */
public class NamedThreadFactory implements ThreadFactory {

    // constants -----------------------------------------------------------------

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    public static final String THREADFACTORY = "threadfactory-";
    public static final String GROUPNUMBER = "-groupnumber-";
    public static final String THREAD = "-thread-";
    public static final String PARENTGROUP = "parentgroup-";
    public static final String GROUPNAME = "-groupname-";

    // internal vars -------------------------------------------------------------

    private final ThreadGroup parentGroup;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String factoryName;
    private final int factoryGroupNumber;

    // constructors --------------------------------------------------------------

    public NamedThreadFactory() {
        this("not named");
    }

    public NamedThreadFactory(String factoryName) {
        SecurityManager s = System.getSecurityManager();
        this.parentGroup = (s != null) ? s.getThreadGroup() :
                     Thread.currentThread().getThreadGroup();
        this.factoryGroupNumber = POOL_NUMBER.getAndIncrement();
        this.factoryName = factoryName;
        this.group = new ThreadGroup(this.parentGroup, PARENTGROUP + parentGroup.getName() +
                GROUPNAME + factoryName + factoryGroupNumber);

    }

    // ThreadFactory -------------------------------------------------------------

    public Thread newThread(Runnable r) {

        String threadName = THREADFACTORY + this.factoryName + GROUPNUMBER +
                          factoryGroupNumber + THREAD + this.threadNumber.getAndIncrement() + "]";

        Thread t = new Thread(this.group, r, threadName, 0L);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}