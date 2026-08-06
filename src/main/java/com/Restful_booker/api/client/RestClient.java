package com.Restful_booker.api.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Thin, generic HTTP wrapper around REST Assured. Knows nothing about any
 * concrete API — services compose it with an endpoint and a spec.
 * <p>
 * Every call is logged (method, path, status, duration), which is what makes
 * the Allure "Execution log" attachment readable.
 */
public class RestClient {

    private static final Logger LOG = LogManager.getLogger(RestClient.class);

    public Response get(RequestSpecification spec, String path) {
        return execute("GET", path, () -> given().spec(spec).when().get(path));
    }

    public Response get(RequestSpecification spec, String path, Map<String, ?> pathParams) {
        return execute("GET", path, () -> given().spec(spec).pathParams(pathParams).when().get(path));
    }

    public Response post(RequestSpecification spec, String path, Object body) {
        return execute("POST", path, () -> given().spec(spec).body(body).when().post(path));
    }

    public Response put(RequestSpecification spec, String path, Object body, Map<String, ?> pathParams) {
        return execute("PUT", path, () -> given().spec(spec).pathParams(pathParams).body(body).when().put(path));
    }

    public Response patch(RequestSpecification spec, String path, Object body, Map<String, ?> pathParams) {
        return execute("PATCH", path, () -> given().spec(spec).pathParams(pathParams).body(body).when().patch(path));
    }

    public Response delete(RequestSpecification spec, String path, Map<String, ?> pathParams) {
        return execute("DELETE", path, () -> given().spec(spec).pathParams(pathParams).when().delete(path));
    }

    private Response execute(String method, String path, java.util.function.Supplier<Response> call) {
        LOG.info("--> {} {}", method, path);
        long start = System.currentTimeMillis();
        Response response = call.get();
        LOG.info("<-- {} {} responded {} in {} ms",
                method, path, response.statusCode(), System.currentTimeMillis() - start);
        return response;
    }
}
