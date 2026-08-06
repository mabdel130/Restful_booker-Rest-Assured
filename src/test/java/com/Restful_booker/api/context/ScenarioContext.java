package com.Restful_booker.api.context;

import com.Restful_booker.api.models.request.Booking;
import io.restassured.response.Response;
import lombok.Data;

/**
 * Per-scenario state shared between hooks and step definitions.
 * PicoContainer creates a fresh instance for every scenario, which makes
 * parallel execution thread-safe by construction — no static state.
 */
@Data
public class ScenarioContext {

    private String token;
    private Integer bookingId;
    private Response lastResponse;
    private Booking expectedBooking;
}
