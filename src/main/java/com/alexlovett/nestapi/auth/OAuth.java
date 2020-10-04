package com.alexlovett.nestapi.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.time.Instant;

public interface OAuth {

    @POST("token?grant_type=authorization_code&redirect_uri=https://www.google.com")
    Call<Token_Response> getToken(
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("code") String code);

    @Builder
    @Getter
    class Token_Request {

        @NonNull
        private final String client_id;
        @NonNull
        private final String client_secret;
        @NonNull
        private final String code;
        @Builder.Default
        private final String grant_type = "authorization_code";
        @Builder.Default
        private final String redirectUrl = "https://www.google.com";
    }

    class Token_Response {
        @JsonProperty("access_token")
        @Getter
        private String accessToken;
        @JsonProperty("expires_in")
        private long expires;
        @Getter
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("scope")
        private String scope;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonIgnore
        private final Instant created = Instant.now();
    }
}
