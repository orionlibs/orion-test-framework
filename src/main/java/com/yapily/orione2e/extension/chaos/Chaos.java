package com.yapily.orione2e.extension.chaos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Chaos
{
    /**
     * Maximum artificial latency in milliseconds (randomized between 0..latencyMs).
     */
    long latencyMs() default 100;


    /**
     * Drop probability in [0.0, 1.0]. Example: 0.1 means ~10% requests fail with IOException.
     */
    double dropRate() default 0.0;


    /**
     * Optional random seed to make behavior deterministic for a test run.
     * If negative (default) non-deterministic ThreadLocalRandom is used.
     */
    long seed() default -1L;
}
