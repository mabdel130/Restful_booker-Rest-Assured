package com.Restful_booker.api.services;

import com.Restful_booker.api.constants.EndPoints;
import io.restassured.response.Response;

/** Health-check operations. */
public class HealthService extends BaseService {

    public Response ping() {
        return client.get(defaultSpec(), EndPoints.PING);
    }
}
