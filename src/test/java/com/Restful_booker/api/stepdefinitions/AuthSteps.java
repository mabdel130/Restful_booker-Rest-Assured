package com.Restful_booker.api.stepdefinitions;

import com.Restful_booker.api.assertions.ApiAssertions;
import com.Restful_booker.api.config.ConfigProvider;
import com.Restful_booker.api.context.ScenarioContext;
import com.Restful_booker.api.models.request.CredentialsRequest;
import com.Restful_booker.api.models.response.TokenResponse;
import com.Restful_booker.api.services.AuthService;
import com.Restful_booker.api.utils.TemplateResolver;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class AuthSteps {

    private static final Logger LOG = LogManager.getLogger(AuthSteps.class);

    private final ScenarioContext context;
    private final AuthService authService = new AuthService();

    public AuthSteps(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Values may be literals or {@code ${config.key}} placeholders, so an
     * Examples table can reference configured credentials instead of hard-coding them.
     */
    @When("I request an auth token with username {string} and password {string}")
    public void iRequestAnAuthTokenWith(String username, String password) {
        CredentialsRequest credentials = CredentialsRequest.builder()
                .username(TemplateResolver.resolve(username))
                .password(TemplateResolver.resolve(password))
                .build();
        Assert.assertNotNull(credentials.getUsername(), "Username resolved to null");
        Assert.assertNotNull(credentials.getPassword(), "Password resolved to null");

        LOG.info("Requesting auth token for user '{}'", credentials.getUsername());
        context.setLastResponse(authService.authenticate(credentials));
        Assert.assertNotNull(context.getLastResponse(), "No response from the auth endpoint");
    }

    @Then("a valid auth token should be returned")
    public void aValidAuthTokenShouldBeReturned() {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasStatusCode(200)
                .hasJsonContentType()
                .hasNonEmptyBody()
                .hasField("token")
                .respondedWithin(ConfigProvider.get().maxResponseTimeMs());

        TokenResponse tokenResponse = context.getLastResponse().as(TokenResponse.class);
        Assert.assertNotNull(tokenResponse.getToken(),
                "Expected a token but got none. Reason: " + tokenResponse.getReason());
        Assert.assertFalse(tokenResponse.getToken().isBlank(), "Token must not be blank");
        Assert.assertNull(tokenResponse.getReason(),
                "A successful auth response must not carry a failure reason");

        context.setToken(tokenResponse.getToken());
        LOG.info("Received a valid auth token");
    }

    @Then("the auth response reason should be {string}")
    public void theAuthResponseReasonShouldBe(String expectedReason) {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasStatusCode(200)
                .hasJsonContentType()
                .fieldEquals("reason", expectedReason);

        TokenResponse tokenResponse = context.getLastResponse().as(TokenResponse.class);
        Assert.assertNull(tokenResponse.getToken(),
                "A rejected authentication must not return a token");
        LOG.info("Authentication correctly rejected: {}", expectedReason);
    }
}
