package com.yapily.orione2e.extension.given_state;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface StateLoader
{
    /**
     * Load the fixture identified by 'fixturePath' (resource path, or whatever the loader expects).
     * The ExtensionContext is provided for context (test class, test method, store, etc).
     */
    void load(String fixturePath, ExtensionContext context) throws Exception;


    /**
     * Optionally undo the fixture that was loaded. Called after the test or after all tests if class-scoped.
     */
    default void cleanup(String fixturePath, ExtensionContext context) throws Exception
    {
        // default no-op
    }
}
