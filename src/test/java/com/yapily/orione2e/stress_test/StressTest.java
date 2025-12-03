package com.yapily.orione2e.stress_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.orione2e.extension.stress.Stress;
import com.yapily.orione2e.extension.stress.StressExtension;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(StressExtension.class)
public class StressTest
{
    private static final AtomicInteger counter = new AtomicInteger(0);


    @Stress(times = 10)
    void stressIncrement()
    {
        int value = counter.incrementAndGet();
        System.out.println("Iteration " + value);
        assertThat(value <= 10).isTrue();
    }
}
