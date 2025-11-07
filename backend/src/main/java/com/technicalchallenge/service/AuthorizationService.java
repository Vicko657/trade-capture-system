package com.technicalchallenge.service;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.exceptions.InActiveException;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.security.ApplicationUserDetails;

/**
 * Authorization service class provides business logic and operations relating
 * to application users access.
 * 
 * <p>
 * Has a validation for user's credientials for logging in and privileges for
 * trade CRUD operations.
 * </p>
 */
@Service
@AllArgsConstructor
public class AuthorizationService {

    @Autowired
    private final ApplicationUserService applicationUserService;

    /**
     * Authorization: Validate user's credentials
     * 
     * 
     * @param username users username
     * @param password users password
     */
    public boolean authenticateUser(String userName, String password) {
        return applicationUserService.validateCredentials(userName, password);
    }

    /**
     * Authorization: Validate User's privileges
     * 
     * 
     * @param userId    users unique identifier
     * @param operation users privileges
     * @param tradeDTO  trade data transfer object
     */
    public boolean validateUserPrivileges(Long userId, String operation, TradeDTO tradeDTO) {

        boolean hasOperations = false;

        // Checks if the user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("User isn't authenticated " + operation);
        }

        // Retieving the user by id
        ApplicationUser user = applicationUserService.getUserById(userId);

        // Checks if user is active
        if (!user.isActive()) {
            throw new InActiveException("User must be active");
        }

        // Check Admin and Superuser have authorized access
        String userType = user.getUserProfile().getUserType().toUpperCase();

        if (userType.equals("ADMIN") || userType.equals("SUPER_USER")) {
            return hasOperations = true;
        }

        // Updated: Checks the granted authorities match the operations - removed enums
        // and fixed database relationship
        hasOperations = auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equalsIgnoreCase(operation));

        // true or false - the user has the right privilige
        if (!hasOperations) {
            throw new AccessDeniedException("User does not have authorization " + operation);
        }

        return hasOperations;

    }

    /**
     * Authorization: Current user login id
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUserDetails userDetails = (ApplicationUserDetails) auth.getPrincipal();

        return userDetails.getId();
    }
}
