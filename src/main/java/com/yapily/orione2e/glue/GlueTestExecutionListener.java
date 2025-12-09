package com.yapily.orione2e.glue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.reflections.Reflections;

public class GlueTestExecutionListener implements TestExecutionListener
{
    @Override
    public void testPlanExecutionStarted(TestPlan testPlan)
    {
        try
        {
            runGlueMethods();
        }
        catch(RuntimeException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to execute glue methods", e);
        }
    }


    private void runGlueMethods() throws Exception
    {
        String scanPackages = System.getProperty("glue.scan.package", "");
        final Reflections reflections = scanPackages.isEmpty()
                        ? new Reflections("com.yapily.orione2e")
                        : new Reflections(scanPackages);
        Set<Class<?>> glueClasses = reflections.getTypesAnnotatedWith(Glue.class);
        for(Class<?> cls : glueClasses)
        {
            for(Method m : cls.getDeclaredMethods())
            {
                if(!m.isAnnotationPresent(Glue.class))
                {
                    continue;
                }
                if(m.getParameterCount() != 0)
                {
                    throw new IllegalStateException("@Glue methods must be no-arg: " + m);
                }
                boolean isStatic = Modifier.isStatic(m.getModifiers());
                Object instance = null;
                if(!isStatic)
                {
                    instance = cls.getDeclaredConstructor().newInstance();
                }
                m.setAccessible(true);
                try
                {
                    m.invoke(instance);
                }
                catch(Exception e)
                {
                    // If you want to continue on errors, change this to log & continue.
                }
            }
        }
    }
}
