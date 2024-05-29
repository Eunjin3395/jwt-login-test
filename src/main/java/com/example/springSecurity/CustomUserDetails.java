package com.example.springSecurity;

import com.example.domain.LoginType;
import com.example.domain.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    Long memberId;
    LoginType loginType;
    RoleType roleType;

    public CustomUserDetails(Long memberId, LoginType loginType, RoleType roleType) {
        this.memberId = memberId;
        this.loginType = loginType;
        this.roleType = roleType;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    // override를 위한 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + roleType);


        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return memberId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
