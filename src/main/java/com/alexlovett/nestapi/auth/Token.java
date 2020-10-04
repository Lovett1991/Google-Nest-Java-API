package com.alexlovett.nestapi.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;

import static java.time.Instant.now;

@Builder
public class Token {
    @NonNull
    @Getter
    private String current;
    @NonNull
    private Instant expiry;
    @Getter
    private final String refresh;

    public Token update(Token update) {
        synchronized (this){
            this.current = update.current;
            this.expiry = expiry;
        }
        return this;
    }

    public boolean hasExpired() {
        return expiry.isAfter(now());
    }
}
