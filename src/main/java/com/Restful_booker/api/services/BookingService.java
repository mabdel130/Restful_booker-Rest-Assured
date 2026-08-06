package com.Restful_booker.api.services;

import com.Restful_booker.api.constants.EndPoints;
import com.Restful_booker.api.models.request.Booking;
import io.restassured.response.Response;

import java.util.Map;

/** CRUD operations on the /booking resource. */
public class BookingService extends BaseService {

    private static final String ID = "id";

    public Response getBookingIds() {
        return client.get(defaultSpec(), EndPoints.BOOKING);
    }

    public Response getBookingById(int bookingId) {
        return client.get(defaultSpec(), EndPoints.BOOKING_BY_ID, Map.of(ID, bookingId));
    }

    public Response createBooking(Booking booking) {
        return client.post(defaultSpec(), EndPoints.BOOKING, booking);
    }

    public Response updateBooking(int bookingId, Booking booking, String token) {
        return client.put(tokenSpec(token), EndPoints.BOOKING_BY_ID, booking, Map.of(ID, bookingId));
    }

    public Response partialUpdateBooking(int bookingId, Booking partialBooking, String token) {
        return client.patch(tokenSpec(token), EndPoints.BOOKING_BY_ID, partialBooking, Map.of(ID, bookingId));
    }

    public Response deleteBooking(int bookingId, String token) {
        return client.delete(tokenSpec(token), EndPoints.BOOKING_BY_ID, Map.of(ID, bookingId));
    }
}
