package com.Restful_booker.api.stepdefinitions;

import com.Restful_booker.api.assertions.ApiAssertions;
import com.Restful_booker.api.config.ConfigProvider;
import com.Restful_booker.api.context.ScenarioContext;
import com.Restful_booker.api.services.HealthService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/** Cross-cutting steps reusable by every feature. */
public class CommonSteps {

    private static final Logger LOG = LogManager.getLogger(CommonSteps.class);

    private final ScenarioContext context;
    private final HealthService healthService = new HealthService();

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I ping the API")
    public void iPingTheApi() {
        LOG.info("Pinging the API health endpoint");
        context.setLastResponse(healthService.ping());
        Assert.assertNotNull(context.getLastResponse(), "Ping returned no response object");
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasStatusCode(expectedStatusCode)
                .respondedWithin(ConfigProvider.get().maxResponseTimeMs());
    }

    @Then("the response field {string} should be {string}")
    public void theResponseFieldShouldBe(String jsonPath, String expectedValue) {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasJsonContentType()
                .hasField(jsonPath)
                .fieldEquals(jsonPath, expectedValue);
    }

    @Then("the response should be valid JSON")
    public void theResponseShouldBeValidJson() {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasJsonContentType()
                .hasNonEmptyBody();
    }
}
