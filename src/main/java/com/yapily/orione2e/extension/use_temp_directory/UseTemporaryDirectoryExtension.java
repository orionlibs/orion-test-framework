package com.yapily.orione2e.extension.use_temp_directory;

import com.yapily.orione2e.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UseTemporaryDirectoryExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver
{
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(UseTemporaryDirectoryExtension.class);
    private static final String TEMP_DIR_KEY = "temp-dir";
    private static final String PREV_USER_DIR_KEY = "prev-user-dir";
    // ----- lifecycle -----


    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        // if annotation is present at class level and scope == CLASS, create dir
        Optional<UseTempDirectory> classAnno = context.getTestClass().map(c -> c.getAnnotation(UseTempDirectory.class));
        if(classAnno.isPresent() && classAnno.get().scope() == UseTempDirectory.Scope.CLASS)
        {
            createAndStoreTempDirForClass(context, classAnno.get());
        }
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception
    {
        // cleanup class-scoped temp dir
        Optional<UseTempDirectory> classAnno = context.getTestClass().map(c -> c.getAnnotation(UseTempDirectory.class));
        if(classAnno.isPresent() && classAnno.get().scope() == UseTempDirectory.Scope.CLASS)
        {
            cleanupAndRemoveForClass(context, classAnno.get());
        }
    }


    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        // check for method-level annotation (method-level wins), otherwise class-level METHOD scope applies
        Optional<UseTempDirectory> methodAnno = context.getTestMethod().map(m -> m.getAnnotation(UseTempDirectory.class));
        if(methodAnno.isPresent() && methodAnno.get().scope() == UseTempDirectory.Scope.METHOD)
        {
            createAndStoreTempDirForMethod(context, methodAnno.get());
            return;
        }
        // If no method-level annotation but class-level annotation with METHOD scope exists, create per-method dir
        Optional<UseTempDirectory> classAnno = context.getTestClass().map(c -> c.getAnnotation(UseTempDirectory.class));
        if(classAnno.isPresent() && classAnno.get().scope() == UseTempDirectory.Scope.METHOD)
        {
            createAndStoreTempDirForMethod(context, classAnno.get());
        }
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        // if method stored a temp dir, cleanup
        ExtensionContext.Store store = storeForMethod(context);
        Path dir = store.remove(TEMP_DIR_KEY, Path.class);
        if(dir != null)
        {
            // restore user.dir if we changed it
            String prev = store.remove(PREV_USER_DIR_KEY, String.class);
            if(prev != null)
            {
                System.setProperty("user.dir", prev);
            }
            // cleanup directory
            try
            {
                FileUtils.deleteRecursively(dir);
            }
            catch(IOException e)
            {
                // best-effort cleanup; don't hide test errors, but log to stderr for debug
                System.err.println("Failed to delete temp dir " + dir + ": " + e);
            }
        }
        // If no method-level temp dir, nothing to do (class-level may handle cleanup later)
    }
    // ----- helper methods for creation/cleanup -----


    private void createAndStoreTempDirForClass(ExtensionContext context, UseTempDirectory cfg)
    {
        ExtensionContext.Store store = storeForClass(context);
        // avoid double-create
        if(store.get(TEMP_DIR_KEY, Path.class) != null)
        {
            return;
        }
        Path dir = createTempDir(context.getRequiredTestClass().getSimpleName(), cfg.prefix());
        store.put(TEMP_DIR_KEY, dir);
        if(cfg.setAsUserDir())
        {
            String prev = System.getProperty("user.dir");
            store.put(PREV_USER_DIR_KEY, prev);
            System.setProperty("user.dir", dir.toString());
        }
    }


    private void cleanupAndRemoveForClass(ExtensionContext context, UseTempDirectory cfg)
    {
        ExtensionContext.Store store = storeForClass(context);
        Path dir = store.remove(TEMP_DIR_KEY, Path.class);
        if(dir != null)
        {
            // restore user.dir if we changed it
            String prev = store.remove(PREV_USER_DIR_KEY, String.class);
            if(prev != null)
            {
                System.setProperty("user.dir", prev);
            }
            try
            {
                FileUtils.deleteRecursively(dir);
            }
            catch(IOException e)
            {
                System.err.println("Failed to delete class temp dir " + dir + ": " + e);
            }
        }
    }


    private void createAndStoreTempDirForMethod(ExtensionContext context, UseTempDirectory cfg)
    {
        ExtensionContext.Store store = storeForMethod(context);
        // Avoid double create
        if(store.get(TEMP_DIR_KEY, Path.class) != null)
        {
            return;
        }
        String baseName = context.getRequiredTestMethod().getName();
        Path dir = createTempDir(baseName, cfg.prefix());
        store.put(TEMP_DIR_KEY, dir);
        if(cfg.setAsUserDir())
        {
            String prev = System.getProperty("user.dir");
            store.put(PREV_USER_DIR_KEY, prev);
            System.setProperty("user.dir", dir.toString());
        }
    }


    private Path createTempDir(String baseName, String prefix)
    {
        try
        {
            String safeBase = sanitizeName(baseName);
            String unique = UUID.randomUUID().toString().replace("-", "");
            String name = prefix + safeBase + "-" + unique;
            return Files.createTempDirectory(name);
        }
        catch(IOException e)
        {
            throw new RuntimeException("Unable to create temporary directory", e);
        }
    }


    private String sanitizeName(String s)
    {
        return s == null ? "anon" : s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    // ----- ParameterResolver -----


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException
    {
        Parameter p = parameterContext.getParameter();
        Class<?> t = p.getType();
        return Path.class.equals(t) || File.class.equals(t) || String.class.equals(t);
    }


    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException
    {
        // prefer method store, then class store
        Path dir = storeForMethod(extensionContext).get(TEMP_DIR_KEY, Path.class);
        if(dir == null)
        {
            dir = storeForClass(extensionContext).get(TEMP_DIR_KEY, Path.class);
        }
        if(dir == null)
        {
            throw new ParameterResolutionException("No temporary directory available; annotate test with @UseTemporaryDirectory");
        }
        Class<?> t = parameterContext.getParameter().getType();
        if(Path.class.equals(t))
        {
            return dir;
        }
        if(File.class.equals(t))
        {
            return dir.toFile();
        }
        if(String.class.equals(t))
        {
            return dir.toString();
        }
        throw new ParameterResolutionException("Unsupported parameter type: " + t);
    }
    // ----- stores -----


    private ExtensionContext.Store storeForMethod(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestMethod()));
    }


    private ExtensionContext.Store storeForClass(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestClass()));
    }
}
