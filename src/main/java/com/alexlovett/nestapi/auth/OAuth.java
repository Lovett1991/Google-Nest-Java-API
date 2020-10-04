package com.alexlovett.nestapi.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.time.Instant;
import java.util.function.Function;

public interface OAuth {

    @POST("token?grant_type=authorization_code&redirect_uri=https://www.google.com")
    Call<Token_Response> getTokenForCode(
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("code") String code);

    @POST("token?grant_type=refresh_token")
    Call<Token_Response> getTokenWithRefresh(
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("refresh_token") String refreshToken);

    @FunctionalInterface
    interface TokenRefresher extends Function<Token, Token> {}

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

        public Token toToken(){
            return Token.builder()
                    .current(accessToken)
                    .refresh(refreshToken)
                    .expiry(created.plusMillis(expires))
                    .build();
        }
    }
}
