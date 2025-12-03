package com.yapily.orione2e.extension.lifecycle;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.reflections.Reflections;

public class BeforeEachTestExecutionListener implements BeforeEachCallback
{
    private static final List<GivenMethodInvoker> CACHED_INVOKERS = new ArrayList<>();
    private static final AtomicBoolean INIT = new AtomicBoolean(false);


    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        if(context.getTestClass().isPresent())
        {
            ensureInitialized(context.getTestClass().get());
            for(GivenMethodInvoker invoker : CACHED_INVOKERS)
            {
                invoker.invoke();
            }
        }
    }


    private void ensureInitialized(Class<?> testClass)
    {
        if(INIT.get())
        {
            return;
        }
        synchronized(INIT)
        {
            if(INIT.get())
            {
                return;
            }
            String scanPackages = System.getProperty("given.scan.package", "");
            final Reflections reflections = scanPackages.isEmpty()
                            ? new Reflections("com.yapily.orione2e")
                            : new Reflections(scanPackages);
            Set<Class<?>> givenClasses = reflections.getTypesAnnotatedWith(Given.class);
            List<GivenMethodInvoker> invokers = new ArrayList<>();
            for(Class<?> cls : givenClasses)
            {
                Given givenAnnotation = cls.getAnnotation(Given.class);
                if(givenAnnotation.testClass().getName().equals(Void.class.getName()) || givenAnnotation.testClass().getName().equals(testClass.getName()))
                {
                    for(Method m : cls.getDeclaredMethods())
                    {
                        if(!m.isAnnotationPresent(Given.class))
                        {
                            continue;
                        }
                        if(m.getParameterCount() != 0)
                        {
                            throw new IllegalStateException("@Given methods must be no-arg: " + m);
                        }
                        boolean isStatic = Modifier.isStatic(m.getModifiers());
                        m.setAccessible(true);
                        invokers.add(new GivenMethodInvoker(cls, m, isStatic));
                    }
                }
            }
            // preserve discovery order for predictability
            CACHED_INVOKERS.addAll(invokers);
            INIT.set(true);
        }
    }


    private static class GivenMethodInvoker
    {
        private final Class<?> declaringClass;
        private final Method method;
        private final boolean isStatic;


        GivenMethodInvoker(Class<?> declaringClass, Method method, boolean isStatic)
        {
            this.declaringClass = declaringClass;
            this.method = method;
            this.isStatic = isStatic;
        }


        void invoke() throws Exception
        {
            Object instance = null;
            if(!isStatic)
            {
                instance = declaringClass.getDeclaredConstructor().newInstance();
            }
            try
            {
                method.invoke(instance);
            }
            catch(ReflectiveOperationException roe)
            {
                // unwrap target exception (so JUnit shows the original cause)
                Throwable t = roe.getCause() != null ? roe.getCause() : roe;
                if(t instanceof Exception)
                {
                    throw (Exception)t;
                }
                else
                {
                    throw new RuntimeException(t);
                }
            }
        }


        @Override
        public String toString()
        {
            return declaringClass.getName() + "#" + method.getName();
        }
    }
}
