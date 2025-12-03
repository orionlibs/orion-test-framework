package com.yapily.orione2e.extension.requires_resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(RequiresResources.class)
public @interface RequiresResource
{
    String host();


    int port();


    /** Timeout in milliseconds to attempt connecting to host:port. */
    long timeoutMs() default 500;


    String description() default "";
}
