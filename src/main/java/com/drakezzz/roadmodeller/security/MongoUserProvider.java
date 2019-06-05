package com.drakezzz.roadmodeller.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MongoUserProvider {

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    public boolean supports(Class<?> aClass) {
        return false;
    }
}
