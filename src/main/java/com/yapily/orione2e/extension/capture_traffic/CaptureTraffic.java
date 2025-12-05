package com.yapily.orione2e.extension.capture_traffic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CaptureTraffic
{
    /**
     * Maximum number of bytes to capture for request/response body snippets.
     * Default 1024 bytes.
     */
    int snippetMaxBytes() default 1024;
}
