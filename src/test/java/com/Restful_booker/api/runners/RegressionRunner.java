package com.Restful_booker.api.runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import org.testng.annotations.Test;

/**
 * Everything that is not smoke. {@code dependsOnGroups = "smoke"} is the
 * TestNG dependency: these scenarios only run once the smoke group has passed.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.Restful_booker.api.stepdefinitions", "com.Restful_booker.api.hooks"},
        tags = "not @smoke and not @ignore",
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class RegressionRunner extends BaseRunner {

    @Test(groups = "regression", dependsOnGroups = "smoke",
            description = "Regression scenarios", dataProvider = "scenarios")
    @Override
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }
}
