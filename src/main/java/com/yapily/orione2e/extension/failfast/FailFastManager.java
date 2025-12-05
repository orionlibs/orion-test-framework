package com.yapily.orione2e.extension.failfast;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FailFastManager
{
    private static final AtomicBoolean TRIGGERED = new AtomicBoolean(false);
    // store the primary cause; further causes will be attached as suppressed exceptions
    private static final AtomicReference<Throwable> CAUSE = new AtomicReference<>(null);


    /**
     * Trigger fail-fast with the provided Throwable.
     * If another cause was already recorded, the new one is added as suppressed to the primary cause.
     */
    public static void trigger(Throwable t)
    {
        Objects.requireNonNull(t, "cause must not be null");
        // attempt to set the cause if absent
        boolean set = CAUSE.compareAndSet(null, t);
        if(!set)
        {
            // primary cause already present; attach as suppressed
            Throwable primary = CAUSE.get();
            try
            {
                primary.addSuppressed(t);
            }
            catch(Exception ignored)
            {
                // ignore failures to add suppressed
            }
        }
        TRIGGERED.set(true);
    }


    /** true if fail-fast has been triggered in this JVM. */
    public static boolean isTriggered()
    {
        return TRIGGERED.get();
    }


    /** Primary cause, if present. */
    public static Optional<Throwable> getCause()
    {
        return Optional.ofNullable(CAUSE.get());
    }
}
