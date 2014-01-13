package com.tacitknowledge.slowlight.embedded.aspect;

import com.tacitknowledge.slowlight.embedded.DegradationHandler;
import com.tacitknowledge.slowlight.embedded.TargetCallback;
import com.tacitknowledge.slowlight.embedded.config.json.JSONConfigBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class DegradationAdviceTest
{
    private static final String TEST_CONFIG_FILE_NAME = "test-slowlight-embedded.config";

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private Signature signature;

    @Before
    public void setup()
    {
        System.setProperty(JSONConfigBuilder.PROPERTY_CONFIG_FILE_NAME, TEST_CONFIG_FILE_NAME);

        doReturn(signature).when(pjp).getSignature();
    }

    @Test
    public void adviceShouldApplyIfAnyClassAndMethodMatch() throws Throwable
    {
        doReturn("test.Class2").when(signature).getDeclaringTypeName();
        doReturn("method2").when(signature).getName();

        final boolean apply = DegradationAdvice.apply(pjp);

        assertThat(apply, is(true));
    }

    @Test
    public void adviceShouldNotApplyIfNoneOfSpecifiedClassAndMethodMatch() throws Throwable
    {
        doReturn("test.Class2").when(signature).getDeclaringTypeName();
        doReturn("method4").when(signature).getName();

        final boolean apply = DegradationAdvice.apply(pjp);

        assertThat(apply, is(false));
    }

    @Test
    public void adviceShouldProceedWithTargetMethod() throws Throwable
    {
        final DegradationHandler handler = mock(DegradationHandler.class);

        final DegradationAdvice degradationAdvice = spy(new DegradationAdvice());
        doReturn(handler).when(degradationAdvice).getDegradationHandler(pjp);

        degradationAdvice.proxyCall(pjp);

        verify(handler).invoke(Matchers.<TargetCallback>anyObject());
    }

    @Test
    public void adviceShouldInvokeSameHandlerInstancePerClassMethod() throws Throwable
    {
        final Set<DegradationHandler> handlers = new HashSet<DegradationHandler>();

        final DegradationAdvice degradationAdvice = spy(new DegradationAdvice());

        doReturn("test.Class2").when(signature).getDeclaringTypeName();
        doReturn("method2").when(signature).getName();
        DegradationAdvice.apply(pjp);

        handlers.add(degradationAdvice.getDegradationHandler(pjp));

        doReturn("test.Class2").when(signature).getDeclaringTypeName();
        doReturn("method3").when(signature).getName();
        DegradationAdvice.apply(pjp);

        handlers.add(degradationAdvice.getDegradationHandler(pjp));

        doReturn("test.Class2").when(signature).getDeclaringTypeName();
        doReturn("method2").when(signature).getName();
        DegradationAdvice.apply(pjp);

        handlers.add(degradationAdvice.getDegradationHandler(pjp));

        assertThat(handlers.size(), is(equalTo(2)));
    }
}
