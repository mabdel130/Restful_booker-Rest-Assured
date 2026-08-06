package com.Restful_booker.api.services;

import com.Restful_booker.api.client.RestClient;
import com.Restful_booker.api.specs.SpecFactory;
import io.restassured.specification.RequestSpecification;

/**
 * Base of the Service Object Model layer (the API analogue of Page Object
 * Model). Each concrete service owns exactly one API resource and exposes
 * business-level operations, never raw HTTP details.
 */
public abstract class BaseService {

    protected final RestClient client = new RestClient();

    protected RequestSpecification defaultSpec() {
        return SpecFactory.defaultSpec();
    }

    protected RequestSpecification tokenSpec(String token) {
        return SpecFactory.tokenSpec(token);
    }
}
