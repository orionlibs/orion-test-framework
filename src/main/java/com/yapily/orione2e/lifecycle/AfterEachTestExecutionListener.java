package com.yapily.orione2e.lifecycle;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.reflections.Reflections;

public class AfterEachTestExecutionListener implements AfterEachCallback
{
    private static final List<AfterEachMethodInvoker> CACHED_INVOKERS = new ArrayList<>();
    private static final AtomicBoolean INIT = new AtomicBoolean(false);


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        if(context.getTestClass().isPresent())
        {
            ensureInitialized(context.getTestClass().get());
            for(AfterEachMethodInvoker invoker : CACHED_INVOKERS)
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
            String scanPackages = System.getProperty("after-each.scan.package", "");
            final Reflections reflections = scanPackages.isEmpty()
                            ? new Reflections("com.yapily.orione2e")
                            : new Reflections(scanPackages);
            Set<Class<?>> afterEachClasses = reflections.getTypesAnnotatedWith(AfterEach.class);
            List<AfterEachMethodInvoker> invokers = new ArrayList<>();
            for(Class<?> cls : afterEachClasses)
            {
                AfterEach afterEachAnnotation = cls.getAnnotation(AfterEach.class);
                if(afterEachAnnotation.testClass().getName().equals(Void.class.getName()) || afterEachAnnotation.testClass().getName().equals(testClass.getName()))
                {
                    for(Method m : cls.getDeclaredMethods())
                    {
                        if(!m.isAnnotationPresent(AfterEach.class))
                        {
                            continue;
                        }
                        if(m.getParameterCount() != 0)
                        {
                            throw new IllegalStateException("@AfterEach methods must be no-arg: " + m);
                        }
                        boolean isStatic = Modifier.isStatic(m.getModifiers());
                        m.setAccessible(true);
                        invokers.add(new AfterEachMethodInvoker(cls, m, isStatic));
                    }
                }
            }
            // preserve discovery order for predictability
            CACHED_INVOKERS.addAll(invokers);
            INIT.set(true);
        }
    }


    private static class AfterEachMethodInvoker
    {
        private final Class<?> declaringClass;
        private final Method method;
        private final boolean isStatic;


        AfterEachMethodInvoker(Class<?> declaringClass, Method method, boolean isStatic)
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
