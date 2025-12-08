package com.yapily.orione2e.extension.performance_budget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface PerfBudget
{
    /**
     * Absolute max latency (ms) for the test. If negative (default) it's ignored.
     */
    long maxMs() default -1L;


    /**
     * Percentile to evaluate against history, in range (0,100]. Example: 90.0 for p90.
     * If <= 0, percentile check is disabled.
     */
    double percentile() default -1.0;


    /**
     * Allowed maximum at the configured percentile (ms). Checked only if percentile() > 0.
     */
    long maxMsAtPercentile() default Long.MAX_VALUE;


    /**
     * Minimum number of historical samples required to evaluate percentile.
     * If there are fewer samples, percentile check is skipped.
     */
    int minSamples() default 10;


    /**
     * Directory to store per-test history files (relative to project root or absolute).
     * Default "perf-history".
     */
    String historyDir() default "perf-history";


    /**
     * If true, append current run to history file. You can disable via system property:
     * -Dperf.updateBaseline=false
     */
    boolean updateBaseline() default true;
}
