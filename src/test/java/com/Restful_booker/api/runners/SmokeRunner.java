package com.Restful_booker.api.runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import org.testng.annotations.Test;

/**
 * Gate stage of the dependency suite: the fast @smoke checks.
 * {@link RegressionRunner} depends on this group, so if the smoke set fails the
 * regression scenarios are skipped instead of wasting time against a broken API.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.Restful_booker.api.stepdefinitions", "com.Restful_booker.api.hooks"},
        tags = "@smoke and not @ignore",
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class SmokeRunner extends BaseRunner {

    @Test(groups = "smoke", description = "Smoke scenarios", dataProvider = "scenarios")
    @Override
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }
}
