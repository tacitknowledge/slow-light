package com.tacitknowledge.slowlight.embedded;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Pavel Sorocun (psorocun@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ProxyBuilderTest
{
    @Test
    public void shouldBuildProxy() throws Exception
    {
        final MethodHandler handler = mock(MethodHandler.class);

        final SimpleConcreteClass instance = new ProxyBuilder()
                .aClass(SimpleConcreteClass.class)
                .handler(handler)
                .build();

        final ProxyObject proxy = (ProxyObject) instance;

        assertThat(handler, is(proxy.getHandler()));
    }

    public static class SimpleConcreteClass {}
}
