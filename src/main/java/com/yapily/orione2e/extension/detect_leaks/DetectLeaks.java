package com.yapily.orione2e.extension.detect_leaks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DetectLeaks
{
    /**
     * Enable thread leak detection (default true).
     */
    boolean checkThreads() default true;


    /**
     * Enable file descriptor leak detection (only supported on Linux via /proc/self/fd).
     * Default true.
     */
    boolean checkFileDescriptors() default true;


    /**
     * Additional thread name patterns to ignore (substring match).
     * Useful to ignore known persistent threads (e.g. "OkHttp", "logback-").
     */
    String[] ignoreThreadNameSubstrings() default {};


    /**
     * Allowed increase in file descriptor count (0 means any increase is considered a leak).
     */
    int allowedFdIncrease() default 0;
}
