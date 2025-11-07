package com.technicalchallenge.service;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.exceptions.InActiveException;
import com.technicalchallenge.exceptions.UnauthorizedAccessException;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.UserType;
import com.technicalchallenge.security.ApplicationUserDetails;

@Service
@AllArgsConstructor
public class AuthorizationService {

    @Autowired
    private final ApplicationUserService applicationUserService;

    public boolean authenticateUser(String userName, String password) {
        return applicationUserService.validateCredentials(userName, password);
    }

    public boolean validateUserPrivileges(Long userId, String operation, TradeDTO tradeDTO) {

        boolean hasOperations = false;

        // Retieving the user by id
        ApplicationUser user = applicationUserService.getUserById(userId);

        // Checks if user is active
        if (!user.isActive()) {
            throw new InActiveException("User must be active");
        }

        // Admin and Superuser have authorized access
        String userType = user.getUserProfile().getUserType().toUpperCase();

        if (userType.equals("ADMIN") || userType.equals("SUPER_USER")) {
            return hasOperations = true;
        }

        // Checks value of user type against the mapped enum of user type with set
        // operations
        UserType userTypeEnum = UserType.valueOf(userType);

        // true or false - the user has the right privilege
        if (!userTypeEnum.isAllowed().contains(operation)) {
            throw new UnauthorizedAccessException("User does not have authorization" + operation);
        } else {
            hasOperations = true;
        }

        return hasOperations;

    }

    // Gets the current user login id
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUserDetails userDetails = (ApplicationUserDetails) auth.getPrincipal();

        return userDetails.getId();
    }
}
