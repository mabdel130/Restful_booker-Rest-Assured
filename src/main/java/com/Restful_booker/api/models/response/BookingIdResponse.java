package com.Restful_booker.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One element of the GET /booking id listing. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingIdResponse {

    private Integer bookingid;
}
