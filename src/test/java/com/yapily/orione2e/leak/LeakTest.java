package com.yapily.orione2e.leak;

import com.yapily.orione2e.extension.detect_leaks.DetectLeaks;
import com.yapily.orione2e.extension.detect_leaks.ResourceLeakDetectorExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ResourceLeakDetectorExtension.class)
public class LeakTest
{
    @Test
    @DetectLeaks(checkThreads = true, checkFileDescriptors = true, ignoreThreadNameSubstrings = {"OkHttp", "logback"}, allowedFdIncrease = 0)
    void noLeak()
    {
        // test code that should not leak threads/fds
    }


    @Test
    @DetectLeaks(checkThreads = true)
    void threadLeak()
    {
        // example of a leaking test:
        new Thread(() -> {
            try
            {
                Thread.sleep(Long.MAX_VALUE);
            }
            catch(InterruptedException ignored)
            {
            }
        }, "my-leak-thread").start();
    }
}
