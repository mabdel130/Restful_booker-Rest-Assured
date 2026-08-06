package com.Restful_booker.api.constants;

/**
 * All resource paths live here — no URL literals anywhere else in the framework.
 * Path parameters use REST Assured placeholder syntax ({@code {id}}).
 */
public final class EndPoints {

    public static final String PING = "/ping";
    public static final String AUTH = "/auth";
    public static final String BOOKING = "/booking";
    public static final String BOOKING_BY_ID = "/booking/{id}";

    private EndPoints() {
    }
}
