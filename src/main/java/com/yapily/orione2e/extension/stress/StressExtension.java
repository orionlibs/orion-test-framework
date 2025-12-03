package com.yapily.orione2e.extension.stress;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

public class StressExtension implements TestTemplateInvocationContextProvider
{
    @Override
    public boolean supportsTestTemplate(ExtensionContext context)
    {
        return context.getTestMethod()
                        .map(m -> m.isAnnotationPresent(Stress.class))
                        .orElse(false);
    }


    @Override
    public Stream<? extends TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context)
    {
        int times = context.getTestMethod()
                        .map(m -> m.getAnnotation(Stress.class).times())
                        .orElse(1);
        return IntStream.rangeClosed(1, times)
                        .mapToObj(i -> new StressInvocationContext(i));
    }


    private static class StressInvocationContext implements TestTemplateInvocationContext
    {
        private final int iteration;


        StressInvocationContext(int iteration)
        {
            this.iteration = iteration;
        }


        @Override
        public String getDisplayName(int invocationIndex)
        {
            return "stress iteration " + iteration;
        }


        @Override
        public List<Extension> getAdditionalExtensions()
        {
            return List.of((BeforeTestExecutionCallback)context -> {
                System.out.println("Starting iteration " + iteration);
            });
        }
    }
}
