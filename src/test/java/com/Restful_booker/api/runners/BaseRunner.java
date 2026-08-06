package com.Restful_booker.api.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.DataProvider;

/**
 * Shared runner configuration.
 * <p>
 * The data provider is always {@code parallel = true}; whether scenarios really
 * run concurrently is decided by {@code data-provider-thread-count} in the
 * selected suite file (1 = sequential). That keeps one runner for both modes.
 */
public abstract class BaseRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
