package com.yapily.orione2e.extension.requires_resource;

import com.yapily.orione2e.utils.NetUtils;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ResourceExecutionCondition implements ExecutionCondition
{
    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult.enabled("@Requires* not present or resource available");


    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context)
    {
        // Check method-level annotation first
        Optional<Method> testMethod = context.getTestMethod();
        if(testMethod.isPresent())
        {
            Method method = testMethod.get();
            RequiresResource[] resources = method.getAnnotationsByType(RequiresResource.class);
            if(resources.length > 0)
            {
                for(RequiresResource rr : resources)
                {
                    ConditionEvaluationResult result = evaluateRequiresResource(rr);
                    if(result.isDisabled())
                    {
                        return result;
                    }
                }
                return ConditionEvaluationResult.enabled("All @RequiresResource checks passed");
            }
        }
        else
        {
            // No test method context (e.g., class-level evaluation): check class-level annotations
            RequiresResource rr = context.getTestClass()
                            .map(c -> c.getAnnotation(RequiresResource.class))
                            .orElse(null);
            if(rr != null)
            {
                return evaluateRequiresResource(rr);
            }
        }
        return ENABLED;
    }


    private ConditionEvaluationResult evaluateRequiresResource(RequiresResource rr)
    {
        String host = rr.host();
        int port = rr.port();
        long timeoutMs = Math.max(1, rr.timeoutMs());
        try
        {
            boolean ok = NetUtils.canConnectTcp(host, port, Duration.ofMillis(timeoutMs));
            if(ok)
            {
                return ConditionEvaluationResult.enabled(String.format("Resource reachable: %s:%d (%s)", host, port, rr.description()));
            }
            else
            {
                return ConditionEvaluationResult.disabled(String.format("Skipped: resource not reachable: %s:%d (%s)", host, port, rr.description()));
            }
        }
        catch(SecurityException se)
        {
            return handleCheckFailure("security exception when testing resource " + host + ":" + port, se);
        }
        catch(Exception e)
        {
            return handleCheckFailure("exception when testing resource " + host + ":" + port + " => " + e, e);
        }
    }


    private ConditionEvaluationResult handleCheckFailure(String msg, Exception e)
    {
        return ConditionEvaluationResult.disabled("Check failed (treating as missing): " + msg + " -> " + e);
    }
}
