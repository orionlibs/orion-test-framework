package com.yapily.orione2e.extension.given_state;

import org.junit.jupiter.api.extension.ExtensionContext;

public class NoOpStateLoader implements StateLoader
{
    @Override
    public void load(String fixturePath, ExtensionContext context)
    {
        /* no-op */
    }
}
