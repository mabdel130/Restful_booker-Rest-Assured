package com.Restful_booker.api.services;

import com.Restful_booker.api.config.ConfigProvider;
import com.Restful_booker.api.constants.EndPoints;
import com.Restful_booker.api.models.request.CredentialsRequest;
import com.Restful_booker.api.models.response.TokenResponse;
import io.restassured.response.Response;

/** Authentication operations against the /auth resource. */
public class AuthService extends BaseService {

    /** Raw auth call — lets tests assert both success and failure responses. */
    public Response authenticate(CredentialsRequest credentials) {
        return client.post(defaultSpec(), EndPoints.AUTH, credentials);
    }

    /** Convenience: authenticate with configured credentials and return the token. */
    public String createToken() {
        CredentialsRequest credentials = CredentialsRequest.builder()
                .username(ConfigProvider.get().username())
                .password(ConfigProvider.get().password())
                .build();
        TokenResponse tokenResponse = authenticate(credentials).as(TokenResponse.class);
        if (tokenResponse.getToken() == null) {
            throw new IllegalStateException(
                    "Authentication failed for configured user: " + tokenResponse.getReason());
        }
        return tokenResponse.getToken();
    }
}
