package com.technicalchallenge.controller;

import com.technicalchallenge.dto.UserDTO;
import com.technicalchallenge.dto.UserPrivilegeDTO;
import com.technicalchallenge.mapper.UserPrivilegeMapper;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserPrivilegeId;
import com.technicalchallenge.service.UserPrivilegeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rest Controller for user roles and privileges pairings.
 * 
 * API endpoints to complete CRUD operations.
 */
@RestController
@RequestMapping("/api/userPrivileges")
@Tag(name = "UserPrivilege", description = "Defines access permissions for the user")
public class UserPrivilegeController {
    private static final Logger logger = LoggerFactory.getLogger(UserPrivilegeController.class);

    @Autowired
    private UserPrivilegeService userPrivilegeService;

    @Autowired
    private UserPrivilegeMapper userPrivilegeMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_USER')")
    @Operation(summary = "Get all user privileges", description = "Retrieves a list of all users privileges in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all user privileges", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<UserPrivilegeDTO> getAllUserPrivileges() {
        logger.info("Fetching all user privileges");
        return userPrivilegeService.getAllUserPrivileges().stream()
                .map(userPrivilegeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_USER')")
    @Operation(summary = "Get user privilege by ID", description = "Retrieves a specific user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User privilege found and returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User privilege not found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid login ID format")
    })
    public ResponseEntity<UserPrivilegeDTO> getUserPrivilegeById(@PathVariable UserPrivilegeId id) {
        logger.debug("Fetching user privilege by id: {}", id);
        Optional<UserPrivilege> userPrivilege = userPrivilegeService.getUserPrivilegeById(id);
        return userPrivilege.map(userPrivilegeMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_USER')")
    @Operation(summary = "Create user's privilege", description = "Establishes a new user privilege")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User privilege created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPrivilegeDTO.class))),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid user privilege data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserPrivilegeDTO> createUserPrivilege(@Valid @RequestBody UserPrivilegeDTO userPrivilegeDTO) {
        logger.info("Creating new user privilege: {}", userPrivilegeDTO);
        UserPrivilege createdUserPrivilege = userPrivilegeService
                .saveUserPrivilege(userPrivilegeMapper.toEntity(userPrivilegeDTO));
        return ResponseEntity.created(URI.create("/api/userPrivileges/" + createdUserPrivilege.getUserId()))
                .body(userPrivilegeMapper.toDto(createdUserPrivilege));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEACTIVATE_USER')")
    @Operation(summary = "Delete user's privilege", description = "Removes a user privilege link from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User privilege deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User privilege not found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteUserPrivilege(@PathVariable UserPrivilegeId id) {
        logger.warn("Deleting user privilege with id: {}", id);
        userPrivilegeService.deleteUserPrivilege(id);
        return ResponseEntity.noContent().build();
    }
}
