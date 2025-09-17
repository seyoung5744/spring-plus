package org.example.expert.config;

import org.example.expert.config.model.CustomUserAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAuthUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CustomUserAuthentication authentication = new CustomUserAuthentication(customUser.userId(), customUser.email(), customUser.role());

        context.setAuthentication(authentication);
        return context;
    }
}
