package com.Restful_booker.api.specs;

import com.Restful_booker.api.config.ConfigManager;
import com.Restful_booker.api.config.ConfigProvider;
import com.Restful_booker.api.constants.AuthType;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Builds request/response specifications from configuration.
 * Everything environment-specific (base URI, timeouts, credentials) comes from
 * {@link ConfigManager}; nothing is hard-coded.
 */
public final class SpecFactory {

    private static final String TOKEN_COOKIE = "token";

    private SpecFactory() {
    }

    /** Unauthenticated JSON spec — the default for public endpoints. */
    public static RequestSpecification defaultSpec() {
        return baseBuilder().build();
    }

    /** Token-cookie authenticated spec (Restful-Booker style). */
    public static RequestSpecification tokenSpec(String token) {
        return authSpec(AuthType.TOKEN, token);
    }

    /**
     * Spec for the given auth scheme. Extend the switch when adding new
     * {@link AuthType}s for other APIs.
     */
    public static RequestSpecification authSpec(AuthType authType, String... credentials) {
        RequestSpecBuilder builder = baseBuilder();
        switch (authType) {
            case TOKEN -> builder.addCookie(TOKEN_COOKIE, credentials[0]);
            case BASIC -> builder.setAuth(io.restassured.RestAssured.preemptive()
                    .basic(credentials[0], credentials[1]));
            case NONE -> { /* no auth */ }
        }
        return builder.build();
    }

    /** Common success-agnostic response expectations (content sanity only). */
    public static ResponseSpecification defaultResponseSpec() {
        return new ResponseSpecBuilder()
                .expectResponseTime(org.hamcrest.Matchers.lessThan(60_000L))
                .build();
    }

    private static RequestSpecBuilder baseBuilder() {
        ConfigManager config = ConfigProvider.get();
        return new RequestSpecBuilder()
                .setBaseUri(config.baseUri())
                .setContentType(ContentType.JSON)
                // single-value Accept header: ContentType.JSON's multi-value accept
                // (application/json, application/javascript, ...) breaks strict servers
                .addHeader("Accept", "application/json")
                .setConfig(timeoutConfig(config))
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(LogDetail.URI))
                .addFilter(new ResponseLoggingFilter(LogDetail.STATUS));
    }

    private static RestAssuredConfig timeoutConfig(ConfigManager config) {
        return RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectionTimeoutMs())
                .setParam("http.socket.timeout", config.socketTimeoutMs()));
    }
}
