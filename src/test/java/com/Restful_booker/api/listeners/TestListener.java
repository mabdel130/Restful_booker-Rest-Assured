package com.Restful_booker.api.listeners;

import com.Restful_booker.api.config.ConfigProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IExecutionListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Execution / suite / test listener.
 * <p>
 * Besides logging suite lifecycle events, it seeds the Allure results directory
 * with {@code environment.properties} (shown in the report's Environment widget)
 * and {@code categories.json} (defect classification), so the report is
 * self-describing about where and how the run happened.
 */
public class TestListener implements IExecutionListener, ISuiteListener, ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);
    private static final Path ALLURE_RESULTS = Path.of("target", "allure-results");

    @Override
    public void onExecutionStart() {
        LOG.info("================ API TEST EXECUTION STARTED ================");
        writeAllureEnvironment();
        copyAllureCategories();
    }

    @Override
    public void onExecutionFinish() {
        LOG.info("================ API TEST EXECUTION FINISHED ===============");
    }

    @Override
    public void onStart(ISuite suite) {
        LOG.info("Suite '{}' started | data-provider threads: {}",
                suite.getName(), suite.getXmlSuite().getDataProviderThreadCount());
    }

    @Override
    public void onFinish(ISuite suite) {
        LOG.info("Suite '{}' finished", suite.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("Results for '{}': passed={} failed={} skipped={}",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAILED: {} | {}", describe(result),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "no message");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIPPED: {} (a dependency failed, or it was filtered out)", describe(result));
    }

    private String describe(ITestResult result) {
        Object[] params = result.getParameters();
        return params.length > 0 ? String.valueOf(params[0]) : result.getName();
    }

    /** Environment widget content for the Allure report. */
    private void writeAllureEnvironment() {
        Properties env = new Properties();
        env.setProperty("Environment", System.getProperty("env", "qa"));
        env.setProperty("Base.URI", ConfigProvider.get().baseUri());
        env.setProperty("Thread.Count", System.getProperty("thread.count", "1"));
        env.setProperty("Cucumber.Tags", System.getProperty("cucumber.filter.tags", "all"));
        env.setProperty("Java.Version", System.getProperty("java.version"));
        env.setProperty("OS", System.getProperty("os.name"));
        try {
            Files.createDirectories(ALLURE_RESULTS);
            try (var out = Files.newOutputStream(ALLURE_RESULTS.resolve("environment.properties"))) {
                env.store(out, "Allure environment information");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write Allure environment information", e);
        }
    }

    /** Defect categories (classpath: allure/categories.json) for the report. */
    private void copyAllureCategories() {
        try (InputStream source = getClass().getClassLoader()
                .getResourceAsStream("allure/categories.json")) {
            if (source == null) {
                return;
            }
            Files.createDirectories(ALLURE_RESULTS);
            Files.writeString(ALLURE_RESULTS.resolve("categories.json"),
                    new String(source.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.warn("Could not publish Allure categories: {}", e.getMessage());
        }
    }
}
