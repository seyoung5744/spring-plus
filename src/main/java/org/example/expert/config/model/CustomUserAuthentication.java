package org.example.expert.config.model;

import lombok.Getter;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserAuthentication implements Authentication {

    private final AuthUser authUser;
    private boolean authenticated;

    public CustomUserAuthentication(Long userId, String email, UserRole userRole) {
        this.authUser = new AuthUser(userId, email, userRole);
        this.authenticated = true; // 기본적으로 인증 완료 상태로 두는 경우가 많음
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleKey = (authUser.getUserRole() != null) ? "ROLE_" + authUser.getUserRole().name() : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(roleKey));
    }

    @Override
    public Object getCredentials() {
        return null; // JWT 인증이라면 비밀번호는 없음
    }

    @Override
    public Object getDetails() {
        return null; // 추가 정보 필요시 넣어도 됨 (ex: remote IP)
    }

    @Override
    public Object getPrincipal() {
        return authUser;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return String.valueOf(authUser.getId());
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
