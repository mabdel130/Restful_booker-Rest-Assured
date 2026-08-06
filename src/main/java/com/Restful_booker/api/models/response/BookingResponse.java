package com.Restful_booker.api.models.response;

import com.Restful_booker.api.models.request.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response of POST /booking — the created id plus an echo of the booking. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Integer bookingid;
    private Booking booking;
}
