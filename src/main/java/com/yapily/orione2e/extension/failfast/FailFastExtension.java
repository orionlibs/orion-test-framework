package com.yapily.orione2e.extension.failfast;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailFastExtension implements TestWatcher, ExecutionCondition
{
    private static final Logger log = LoggerFactory.getLogger(FailFastExtension.class);
    /**
     * System property to allow disabling the behavior at runtime.
     * Usage: -Dfailfast.enabled=false
     */
    private static final String ENABLED_PROP = "failfast.enabled";
    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult.enabled("@FailFast not triggered");


    @Override
    public void testFailed(ExtensionContext context, Throwable cause)
    {
        try
        {
            if(!isFailFastEnabled())
            {
                log.debug("FailFast disabled by system property; ignoring fail-fast trigger.");
                return;
            }
            if(isAnnotatedForFailFast(context))
            {
                log.warn("FailFast: test {} failed and is annotated @FailFast — triggering fail-fast.",
                                context.getDisplayName());
                FailFastManager.trigger(cause);
            }
        }
        catch(Exception e)
        {
            log.error("FailFastExtension encountered an error while handling test failure", e);
        }
    }


    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context)
    {
        // If fail-fast is disabled entirely, always enable
        if(!isFailFastEnabled())
        {
            return ENABLED;
        }
        if(!FailFastManager.isTriggered())
        {
            return ENABLED;
        }
        // Skip if the test itself is the one that triggered fail-fast (we don't want to skip the failing test retroactively).
        Optional<Method> testMethod = context.getTestMethod();
        Optional<Throwable> cause = FailFastManager.getCause();
        String reason = cause.map(t -> String.format("Fail-fast triggered by earlier @FailFast test: %s: %s",
                                        t.getClass().getSimpleName(), t.getMessage()))
                        .orElse("Fail-fast triggered by earlier @FailFast test");
        // Disable all further tests (method-level and class-level)
        return ConditionEvaluationResult.disabled(reason);
    }


    private boolean isAnnotatedForFailFast(ExtensionContext ctx)
    {
        // Check method first
        Optional<Method> maybeMethod = ctx.getTestMethod();
        if(maybeMethod.isPresent())
        {
            Method m = maybeMethod.get();
            if(m.isAnnotationPresent(FailFast.class))
            {
                return true;
            }
            // also allow class-level annotation to mark tests as fail-fast
            Class<?> clazz = m.getDeclaringClass();
            if(clazz.isAnnotationPresent(FailFast.class))
            {
                return true;
            }
            return false;
        }
        else
        {
            // Not method context -- check class-level
            return ctx.getTestClass().map(c -> c.isAnnotationPresent(FailFast.class)).orElse(false);
        }
    }


    private boolean isFailFastEnabled()
    {
        return Boolean.parseBoolean(System.getProperty(ENABLED_PROP, "true"));
    }
}
