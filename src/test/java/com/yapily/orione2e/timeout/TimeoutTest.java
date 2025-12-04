package com.yapily.orione2e.timeout;

import com.yapily.orione2e.extension.timeout.TimeoutOverride;
import com.yapily.orione2e.extension.timeout.TimeoutOverrideExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TimeoutOverrideExtension.class)
public class TimeoutTest
{
    @Test
    @TimeoutOverride(valueMs = 2000L)
    void testLongRunning() throws InterruptedException
    {
        Thread.sleep(1500); //it is OK
    }


    @Test
    @TimeoutOverride(valueMs = 1000L)
    void testTooLong() throws InterruptedException
    {
        Thread.sleep(2000); //it will be aborted
    }
}
