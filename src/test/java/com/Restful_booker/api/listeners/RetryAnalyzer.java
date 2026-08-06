package com.Restful_booker.api.listeners;

import com.Restful_booker.api.config.ConfigProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed scenario up to {@code retry.max.count} times (config key,
 * default 0 = no retries). Useful against flaky public/staging APIs; keep it at
 * 0 for suites where a failure must always be a failure.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRIES = resolveMaxRetries();

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRIES) {
            attempt++;
            LOG.warn("Retrying failed scenario (attempt {} of {})", attempt, MAX_RETRIES);
            return true;
        }
        return false;
    }

    /** Config key {@code retry.max.count}, overridable with -Dretry.max.count=2. */
    private static int resolveMaxRetries() {
        String configured = ConfigProvider.get().getProperty("retry.max.count");
        return configured == null ? 0 : Integer.parseInt(configured.trim());
    }
}
