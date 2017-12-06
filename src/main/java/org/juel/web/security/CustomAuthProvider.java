package org.juel.web.security;

import org.juel.model.UserCredential;
import org.juel.repositories.UsersCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
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
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        Optional<UserCredential> userCredential = Optional.ofNullable(usersCredentialsRepository.findOne(email));
        return userCredential
                .filter(cred -> cred.getPassword().equals(password))
                .map(cred -> new UsernamePasswordAuthenticationToken(email, password, Collections.singletonList(new SimpleGrantedAuthority(cred.getRole()))))
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User not found or uncorrected password"));


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
