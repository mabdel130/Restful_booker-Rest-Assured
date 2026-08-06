package com.Restful_booker.api.runners;

import io.cucumber.testng.CucumberOptions;

/**
 * Default runner — executes every feature except anything tagged {@code @ignore}.
 * Narrow a run from the command line with
 * {@code -Dcucumber.filter.tags="@smoke and not @e2e"}.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.Restful_booker.api.stepdefinitions", "com.Restful_booker.api.hooks"},
        tags = "not @ignore",
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json"
        },
        monochrome = true
)
public class TestRunner extends BaseRunner {
}
