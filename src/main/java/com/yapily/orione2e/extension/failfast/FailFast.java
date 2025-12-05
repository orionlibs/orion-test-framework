package com.yapily.orione2e.extension.failfast;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FailFast
{
    /**
     * Optional human-friendly label for the critical test.
     */
    String value() default "";
}
