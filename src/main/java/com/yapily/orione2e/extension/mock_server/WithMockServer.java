package com.yapily.orione2e.extension.mock_server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WithMockServer
{
    /**
     * If 0 (default) a dynamic free port will be used. Use a fixed port only if you really need it.
     */
    int port() default 0;


    /**
     * Optional description; useful in logs.
     */
    String value() default "";


    /**
     * Optional class that configures the mock server (stubs) before the test(s).
     * Implement MockServerSetup and the extension will instantiate it (no-arg ctor) and call setup(server).
     * Default is NoOp (does nothing).
     */
    Class<? extends MockServerSetup> setup() default NoOpMockServerSetup.class;
}
