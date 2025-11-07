package com.technicalchallenge.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.technicalchallenge.model.ApplicationUser;

import lombok.AllArgsConstructor;

/**
 * User Details (Core Information) for Spring security.
 * 
 * <p>
 * Including user's username, password, role and privileges
 * </p>
 */
@AllArgsConstructor
public class ApplicationUserDetails implements UserDetails {

    private ApplicationUser user;

    /**
     * Implemented pre-authorization for privileges with Granted Authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Maps the user's roles & privilleges established in the database
        return user.getUserProfile().getPrivileges().stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getPrivilege().getName()))
                .collect(Collectors.toList());
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
