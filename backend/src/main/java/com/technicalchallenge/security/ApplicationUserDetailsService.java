package com.technicalchallenge.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.repository.ApplicationUserRepository;

import lombok.AllArgsConstructor;

// Spring security - User Login and Authentication
@Service
@AllArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    // Retrieves the user signed in
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser user = applicationUserRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));

        return new ApplicationUserDetails(user);
    }

}
