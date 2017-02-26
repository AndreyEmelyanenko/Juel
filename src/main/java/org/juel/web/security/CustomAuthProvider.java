package org.juel.web.security;

import org.juel.repositories.UsersCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private UsersCredentialsRepository usersCredentialsRepository;

    @Autowired
    public CustomAuthProvider(UsersCredentialsRepository usersCredentialsRepository) {
        this.usersCredentialsRepository = usersCredentialsRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object cred = authentication.getCredentials();
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Stream.of(authentication.getClass().getInterfaces()).anyMatch(interfazze -> interfazze.getName().equals(Authentication.class.getName()));
    }

}
