package com.Restful_booker.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response of POST /auth: {@code token} on success,
 * {@code reason} (e.g. "Bad credentials") on failure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String token;
    private String reason;
}
