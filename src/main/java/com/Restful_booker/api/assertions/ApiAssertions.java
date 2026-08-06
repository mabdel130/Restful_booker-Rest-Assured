package com.Restful_booker.api.assertions;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.asserts.SoftAssert;

import java.util.Objects;


public final class ApiAssertions {

    private static final Logger LOG = LogManager.getLogger(ApiAssertions.class);

    private final Response response;
    private final SoftAssert soft;

    private ApiAssertions(Response response, SoftAssert soft) {
        this.response = response;
        this.soft = soft;
    }

    public static ApiAssertions assertThat(Response response) {
        Objects.requireNonNull(response, "No response captured — did the request step run?");
        return new ApiAssertions(response, null);
    }

    /** Collects all failures and reports them together on {@link #assertAll()}. */
    public static ApiAssertions assertSoftly(Response response) {
        Objects.requireNonNull(response, "No response captured — did the request step run?");
        return new ApiAssertions(response, new SoftAssert());
    }

    public ApiAssertions hasStatusCode(int expected) {
        return check("status code is " + expected,
                response.statusCode(), expected, "Unexpected HTTP status code");
    }

    public ApiAssertions hasJsonContentType() {
        String contentType = response.getContentType();
        boolean isJson = contentType != null && contentType.toLowerCase().contains("json");
        return check("content type is JSON", isJson, true,
                "Expected a JSON content type but got: " + contentType);
    }

    public ApiAssertions respondedWithin(long maxMillis) {
        long actual = response.timeIn(java.util.concurrent.TimeUnit.MILLISECONDS);
        return check("response time " + actual + "ms is below " + maxMillis + "ms",
                actual < maxMillis, true,
                "Response took " + actual + "ms, limit is " + maxMillis + "ms");
    }

    public ApiAssertions hasNonEmptyBody() {
        String body = response.getBody().asString();
        return check("body is not empty", body != null && !body.isBlank(), true,
                "Response body was empty");
    }

    public ApiAssertions hasField(String jsonPath) {
        return check("field '" + jsonPath + "' is present",
                response.jsonPath().get(jsonPath) != null, true,
                "Missing field in response: " + jsonPath);
    }

    public ApiAssertions fieldEquals(String jsonPath, Object expected) {
        Object actual = response.jsonPath().get(jsonPath);
        return check("field '" + jsonPath + "' equals " + expected,
                String.valueOf(actual), String.valueOf(expected),
                "Unexpected value at json path: " + jsonPath);
    }

    /** Reports every collected soft-assertion failure at once. */
    public void assertAll() {
        if (soft != null) {
            soft.assertAll();
        }
    }

    private ApiAssertions check(String description, Object actual, Object expected, String message) {
        LOG.info("Asserting {}", description);
        Allure.step("Assert " + description, () -> {
            if (soft != null) {
                soft.assertEquals(actual, expected, message);
            } else {
                org.testng.Assert.assertEquals(actual, expected, message);
            }
        });
        return this;
    }
}
