package com.tacitknowledge.slowlight.embedded.aspect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.slowlight.embedded.DefaultDegradationStrategy;
import com.tacitknowledge.slowlight.embedded.DegradationHandler;
import com.tacitknowledge.slowlight.embedded.DegradationStrategy;
import com.tacitknowledge.slowlight.embedded.NamedThreadFactory;
import com.tacitknowledge.slowlight.embedded.TargetCallback;
import com.tacitknowledge.slowlight.embedded.config.RuleConfig;
import com.tacitknowledge.slowlight.embedded.config.json.JSONConfigBuilder;


/**
 * Degradation aspect class. This aspect is used to advice any given service/method with degradation logic.
 * Degradation configuration is provided via configuration classes obtained by configuration builder
 * (for ex. see {@link com.tacitknowledge.slowlight.embedded.config.MainConfig}).
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 *
 * @see com.tacitknowledge.slowlight.embedded.DegradationStrategy
 * @see com.tacitknowledge.slowlight.embedded.DegradationHandler
 */
@Aspect
public class DegradationAdvice
{
    private static final Logger LOG = LoggerFactory.getLogger(DegradationAdvice.class);

    private static final JSONConfigBuilder CONFIG_BUILDER = new JSONConfigBuilder();

    private static final Lock handlerCacheLock = new ReentrantLock();
    private static final ConcurrentHashMap<String, DegradationHandler> handlerCache = new ConcurrentHashMap<String, DegradationHandler>();
    private static final String DEGRADATION_ADVICE_THREAD_FACTORY_NAME = "degradation_advice_thread_factory";

    @Pointcut("execution(* *(..)) && !within(com.tacitknowledge.slowlight.embedded.aspect.*) && if()")
    public static boolean apply(final ProceedingJoinPoint pjp)
    {
        boolean apply = false;

        final String jpClassName = pjp.getSignature().getDeclaringTypeName();
        final String jpMethodName = pjp.getSignature().getName();

        for (final RuleConfig ruleConfig : CONFIG_BUILDER.getConfig().getRules())
        {
            apply = ruleConfig.getApplyTo().containsKey(jpClassName) && ruleConfig.getApplyTo().get(jpClassName).contains(jpMethodName);

            if (apply)
            {
                LOG.info("Apply {} aspect to {}.{} method", DegradationAdvice.class.getSimpleName(), jpClassName, jpMethodName);

				try {
					createDegradationHandler(ruleConfig, pjp);
				} catch (ClassNotFoundException e) {
					LOG.error("Could not create DegradationHandler", e);
				}
                break;
            }
        }

        return apply;
    }

    @Around("apply(pjp)")
    public Object proxyCall(final ProceedingJoinPoint pjp) throws Throwable
    {
        final DegradationHandler handler = getDegradationHandler(pjp);

        return handler.invoke(new TargetCallback()
        {
            @Override
            public Object execute() throws Exception
            {
                LOG.info("Execute callback ... ");

                try
                {
                    return pjp.proceed();
                }
                catch (Throwable t)
                {
                    if (t instanceof Exception)
                    {
                        throw (Exception) t;
                    }
                    else
                    {
                        throw new RuntimeException(t);
                    }
                }
            }
        });
    }

    protected DegradationHandler getDegradationHandler(final ProceedingJoinPoint pjp)
    {
        return handlerCache.get(getHandlerKey(pjp));
    }

    private static String getHandlerKey(final ProceedingJoinPoint pjp)
    {
        final String jpClassName = pjp.getSignature().getDeclaringTypeName();
        final String jpMethodName = pjp.getSignature().getName();

        return jpClassName + "." + jpMethodName;
    }

	private static void createDegradationHandler(final RuleConfig ruleConfig,
	        final ProceedingJoinPoint pjp) throws ClassNotFoundException
    {
        handlerCacheLock.lock();
        try
        {
            final String handlerKey = getHandlerKey(pjp);
            if (!handlerCache.containsKey(handlerKey))
            {
                handlerCache.put(handlerKey, initDegradationHandler(ruleConfig));
            }
        }
        finally
        {
            handlerCacheLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
	private static DegradationHandler initDegradationHandler(
	        final RuleConfig ruleConfig) throws ClassNotFoundException
    {
        final DegradationStrategy degradationStrategy = new DefaultDegradationStrategy(ruleConfig.getServiceDemandTime(),
		        ruleConfig.getServiceTimeout(), ruleConfig.getPassRate(),
		        ruleConfig.getRandomExceptionsAsClasses()
		                .toArray(new Class[] {}));

        final ThreadFactory threadFactory = new NamedThreadFactory(DEGRADATION_ADVICE_THREAD_FACTORY_NAME);
        final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ruleConfig.getThreads(),
                threadFactory);

        return new DegradationHandler(executorService, degradationStrategy);
    }
}
