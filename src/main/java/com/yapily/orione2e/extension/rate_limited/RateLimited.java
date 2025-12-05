package com.yapily.orione2e.extension.rate_limited;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RateLimited
{
    /**
     * Minimum interval in milliseconds between the start of two invocations in the same bucket (key).
     */
    long minIntervalMs();


    /**
     * Optional bucket key. Tests that share the same key will be rate-limited together.
     * Default is "default".
     */
    String key() default "default";
}
