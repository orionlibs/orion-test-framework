package com.yapily.orione2e.extension.use_temp_directory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface UseTempDirectory
{
    /**
     * Prefix for the created temp directory name.
     */
    String prefix() default "test-";


    /**
     * Scope: METHOD -> create one temp dir per test method.
     *        CLASS  -> create one temp dir per test class (created in beforeAll, removed in afterAll).
     */
    Scope scope() default Scope.METHOD;


    /**
     * If true, temporarily set System property "user.dir" to the created temp directory
     * while the test (or test class) runs. Restored afterwards.
     */
    boolean setAsUserDir() default false;


    enum Scope
    {
        METHOD,
        CLASS
    }
}
