package com.yapily.orione2e.extension.detect_leaks;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ResourceLeakDetectorExtension implements BeforeEachCallback, AfterEachCallback
{
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(ResourceLeakDetectorExtension.class);
    private static final String THREAD_IDS_KEY = "leak-thread-ids";
    private static final String FD_COUNT_KEY = "leak-fd-count";
    private static final ThreadMXBean THREAD_MX = ManagementFactory.getThreadMXBean();


    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        DetectLeaks cfg = findAnnotation(context);
        if(cfg == null)
        {
            return;
        }
        ExtensionContext.Store store = getStore(context);
        // snapshot thread ids present before test
        long[] ids = THREAD_MX.getAllThreadIds();
        Set<Long> before = Arrays.stream(ids).boxed().collect(Collectors.toSet());
        store.put(THREAD_IDS_KEY, before);
        // snapshot fd count, if supported
        if(cfg.checkFileDescriptors())
        {
            Integer fdCount = tryCountOpenFileDescriptors();
            store.put(FD_COUNT_KEY, fdCount);
        }
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        DetectLeaks cfg = findAnnotation(context);
        if(cfg == null)
        {
            return;
        }
        ExtensionContext.Store store = getStore(context);
        List<String> errors = new ArrayList<>();
        // THREADS
        if(cfg.checkThreads())
        {
            @SuppressWarnings("unchecked")
            Set<Long> before = store.remove(THREAD_IDS_KEY, Set.class);
            if(before == null)
            {
                before = Collections.emptySet();
            }
            // get current threads
            long[] currentIds = THREAD_MX.getAllThreadIds();
            Set<Long> currSet = Arrays.stream(currentIds).boxed().collect(Collectors.toSet());
            // threads that are new (in curr and not in before)
            Set<Long> finalBefore = before;
            Set<Long> newThreads = currSet.stream().filter(id -> !finalBefore.contains(id)).collect(Collectors.toSet());
            // filter alive non-daemon threads and ignore by name substrings
            List<LeakedThread> leaks = new ArrayList<>();
            Set<String> ignoreSubstrings = new HashSet<>(Arrays.asList(cfg.ignoreThreadNameSubstrings()));
            for(Long tid : newThreads)
            {
                ThreadInfo info = THREAD_MX.getThreadInfo(tid, Integer.MAX_VALUE);
                if(info == null)
                {
                    continue; // thread disappeared meanwhile
                }
                String name = info.getThreadName();
                boolean daemon = isThreadDaemon(tid);
                if(daemon)
                {
                    continue; // daemon threads are okay
                }
                if(matchesIgnore(name, ignoreSubstrings))
                {
                    continue;
                }
                // consider it leaked if alive and not in before snapshot
                leaks.add(new LeakedThread(tid, name, info));
            }
            if(!leaks.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Detected ").append(leaks.size()).append(" leaked non-daemon thread(s):\n");
                for(LeakedThread lt : leaks)
                {
                    sb.append(" - ").append(lt.name).append(" (id=").append(lt.id).append(")\n");
                    ThreadInfo ti = lt.info;
                    if(ti != null)
                    {
                        sb.append("   state=").append(ti.getThreadState()).append("\n");
                        if(ti.getStackTrace() != null && ti.getStackTrace().length > 0)
                        {
                            sb.append("   stack:\n");
                            for(StackTraceElement ste : ti.getStackTrace())
                            {
                                sb.append("     at ").append(ste.toString()).append("\n");
                            }
                        }
                    }
                }
                errors.add(sb.toString());
            }
        }
        // FILE DESCRIPTORS (Linux /proc/self/fd)
        if(cfg.checkFileDescriptors())
        {
            Integer beforeCount = store.remove(FD_COUNT_KEY, Integer.class);
            Integer afterCount = tryCountOpenFileDescriptors();
            if(beforeCount != null && afterCount != null)
            {
                int allowed = Math.max(0, cfg.allowedFdIncrease());
                if(afterCount - beforeCount > allowed)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append("File descriptor count increased from ").append(beforeCount)
                                    .append(" to ").append(afterCount)
                                    .append(" (allowed increase = ").append(allowed).append(")\n");
                    // attempt to list opened fds for debugging
                    sb.append(listOpenFileDescriptorTargets());
                    errors.add(sb.toString());
                }
            }
            else if(beforeCount == null && afterCount != null)
            {
                errors.add("Could not snapshot FD count before test, but /proc/self/fd is available now; FD after=" + afterCount);
            }
            else
            {
                // both null -> /proc not present; nothing to do
            }
        }
        if(!errors.isEmpty())
        {
            AssertionError leakError = new AssertionError("Resource leaks detected:\n" + String.join("\n", errors));
            Optional<Throwable> testException = context.getExecutionException();
            if(testException.isPresent())
            {
                // attach leak error as suppressed and rethrow original so original failure remains primary
                Throwable original = testException.get();
                original.addSuppressed(leakError);
                try
                {
                    throw original;
                }
                catch(Throwable e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                throw leakError;
            }
        }
    }
    // ---------- helpers ----------


    private boolean matchesIgnore(String name, Set<String> ignoreSubstrings)
    {
        if(name == null)
        {
            return false;
        }
        for(String s : ignoreSubstrings)
        {
            if(s != null && !s.isEmpty() && name.contains(s))
            {
                return true;
            }
        }
        // also ignore well-known JVM threads that are not leaks
        if(name.startsWith("Reference Handler") || name.startsWith("Finalizer") || name.startsWith("Signal Dispatcher"))
        {
            return true;
        }
        return false;
    }


    private boolean isThreadDaemon(long threadId)
    {
        // There is no direct daemon flag via ThreadMXBean. Use Thread.getAllStackTraces() to find thread object.
        for(Thread t : Thread.getAllStackTraces().keySet())
        {
            if(t.getId() == threadId)
            {
                return t.isDaemon();
            }
        }
        // fallback: if we can't find the Thread object, treat as non-daemon (safer to report)
        return false;
    }


    private Integer tryCountOpenFileDescriptors()
    {
        Path p = Paths.get("/proc/self/fd");
        try
        {
            if(Files.isDirectory(p))
            {
                try(Stream<Path> s = Files.list(p))
                {
                    long count = s.count();
                    // count is small; fit in int safely
                    return (int)count;
                }
            }
        }
        catch(SecurityException | IOException ignored)
        {
            // ignore and return null to indicate not available
        }
        return null;
    }


    private String listOpenFileDescriptorTargets()
    {
        Path p = Paths.get("/proc/self/fd");
        StringBuilder sb = new StringBuilder();
        try
        {
            if(Files.isDirectory(p))
            {
                sb.append("Open fds and targets (limited):\n");
                try(Stream<Path> s = Files.list(p))
                {
                    s.limit(200).forEach(fd -> {
                        try
                        {
                            Path target = Files.readSymbolicLink(fd);
                            sb.append("  ").append(fd.getFileName()).append(" -> ").append(target).append("\n");
                        }
                        catch(Exception e)
                        {
                            sb.append("  ").append(fd.getFileName()).append(" -> <unreadable:").append(e.getMessage()).append(">\n");
                        }
                    });
                }
            }
            else
            {
                sb.append("No /proc/self/fd support on this OS; cannot list fds.\n");
            }
        }
        catch(SecurityException | IOException e)
        {
            sb.append("Failed to list /proc/self/fd: ").append(e.getMessage()).append("\n");
        }
        return sb.toString();
    }


    private ExtensionContext.Store getStore(ExtensionContext ctx)
    {
        return ctx.getStore(NS);
    }


    private DetectLeaks findAnnotation(ExtensionContext ctx)
    {
        // method first
        Optional<DetectLeaks> mAnn = ctx.getTestMethod().map(m -> m.getAnnotation(DetectLeaks.class));
        if(mAnn.isPresent())
        {
            return mAnn.get();
        }
        return ctx.getTestClass().map(c -> c.getAnnotation(DetectLeaks.class)).orElse(null);
    }


    private static class LeakedThread
    {
        final long id;
        final String name;
        final ThreadInfo info;


        LeakedThread(long id, String name, ThreadInfo info)
        {
            this.id = id;
            this.name = name;
            this.info = info;
        }
    }
}
