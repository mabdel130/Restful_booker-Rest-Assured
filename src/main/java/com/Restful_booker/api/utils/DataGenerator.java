package com.Restful_booker.api.utils;

import com.Restful_booker.api.models.request.Booking;
import com.Restful_booker.api.models.request.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Datafaker-based runtime data generation — guarantees unique payloads so
 * parallel runs never collide on identical data.
 */
public final class DataGenerator {

    private static final Faker FAKER = new Faker();
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private DataGenerator() {
    }

    public static Booking randomBooking() {
        LocalDate checkin = LocalDate.now().plusDays(FAKER.number().numberBetween(1, 30));
        return Booking.builder()
                .firstname(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .totalprice(FAKER.number().numberBetween(50, 2000))
                .depositpaid(FAKER.bool().bool())
                .bookingdates(BookingDates.builder()
                        .checkin(checkin.format(ISO_DATE))
                        .checkout(checkin.plusDays(FAKER.number().numberBetween(1, 14)).format(ISO_DATE))
                        .build())
                .additionalneeds(FAKER.food().dish())
                .build();
    }
}
