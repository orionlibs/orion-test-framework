package com.yapily.orione2e.extension.given_state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(GivenStates.class)
public @interface GivenState
{
    /**
     * Path to fixture resource (classpath relative, e.g. "fixtures/user-with-orders.sql").
     */
    String value();


    /**
     * Loader class to apply the fixture. Must implement StateLoader and have a no-arg constructor.
     */
    Class<? extends StateLoader> loader() default NoOpStateLoader.class;
}
