package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.referencedata.UserNotFoundException;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.repository.ApplicationUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ApplicationUserService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationUserService.class);
    private final ApplicationUserRepository applicationUserRepository;

    public boolean validateCredentials(String loginId, String password) {
        logger.debug("Validating credentials for user: {}", loginId);
        Optional<ApplicationUser> user = applicationUserRepository.findByLoginId(loginId);
        return user.map(applicationUser -> applicationUser.getPassword().equals(password)).orElse(false);
    }

    public List<ApplicationUser> getAllUsers() {
        logger.info("Retrieving all users");
        return applicationUserRepository.findAll();
    }

    public ApplicationUser getUserById(Long id) {
        logger.debug("Retrieving user by id: {}", id);

        ApplicationUser user = findUserId(id);
        return user;
    }

    public ApplicationUser saveUser(ApplicationUser user) {
        logger.info("Saving user: {}", user);
        return applicationUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        logger.warn("Deleting user with id: {}", id);
        applicationUserRepository.deleteById(id);
    }

    public ApplicationUser updateUser(Long id, ApplicationUser user) {
        logger.info("Updating user with id: {}", id);
        ApplicationUser existingUser = applicationUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        // Update fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setLoginId(user.getLoginId());
        existingUser.setActive(user.isActive());
        existingUser.setUserProfile(user.getUserProfile());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        // version and lastModifiedTimestamp handled by entity listeners
        return applicationUserRepository.save(existingUser);
    }

    // Checks the Reference Data for Trade Service
    public ApplicationUser findUserId(Long id) {
        return applicationUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("userId", id));
    }

    public ApplicationUser findFirstName(String firstName) {
        return applicationUserRepository.findByFirstName(
                firstName)
                .orElseThrow(() -> new UserNotFoundException("firstName", firstName));
    }

    public ApplicationUser findLoginId(String loginId) {
        logger.debug("Retrieving user by login id: {}", loginId);
        return applicationUserRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("loginId", loginId));
    }

}
