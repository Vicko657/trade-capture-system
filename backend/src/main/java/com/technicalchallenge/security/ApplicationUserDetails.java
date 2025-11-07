package com.technicalchallenge.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.technicalchallenge.model.ApplicationUser;

import lombok.AllArgsConstructor;

// Spring security - User Details Core Information
@AllArgsConstructor
public class ApplicationUserDetails implements UserDetails {

    private ApplicationUser user;

    // Will implement pre-authorization for privileges
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Role_" + user.getUserProfile().getUserType()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return user.getId();
    }

    public String getUserType() {
        return user.getUserProfile().getUserType();
    }

}
