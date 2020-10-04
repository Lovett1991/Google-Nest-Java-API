package com.alexlovett.nestapi.auth;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenStore {

    private final Token token;

    public TokenStore update(Token token){
        token.update(token);
        return this;
    }

}
