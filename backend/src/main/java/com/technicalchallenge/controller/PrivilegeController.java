package com.technicalchallenge.controller;

import com.technicalchallenge.dto.PrivilegeDTO;
import com.technicalchallenge.dto.UserDTO;
import com.technicalchallenge.mapper.PrivilegeMapper;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.service.PrivilegeService;

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
 * Rest Controller for multiple privileges the user has access to.
 * 
 * API endpoints to complete CRUD operations.
 */
@RestController
@RequestMapping("/api/privileges")
@Tag(name = "Privilege", description = "Manages privileges for auhtorization")
public class PrivilegeController {
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PrivilegeMapper privilegeMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_USER')")
    @Operation(summary = "Get all privileges", description = "Retrieves a list of all privileges in the system with their id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all privileges", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PrivilegeDTO> getAllPrivileges() {
        logger.info("Fetching all privileges");
        return privilegeService.getAllPrivileges().stream()
                .map(privilegeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_USER')")
    @Operation(summary = "Get privilege by ID", description = "Retrieves a specific privilege by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Privilege found and returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Privilege not found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<PrivilegeDTO> getPrivilegeById(@PathVariable Long id) {
        logger.debug("Fetching privilege by id: {}", id);
        Optional<Privilege> privilege = privilegeService.getPrivilegeById(id);
        return privilege.map(privilegeMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_USER')")
    @Operation(summary = "Create privilege", description = "Creates a new privilege")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Privilege created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrivilegeDTO.class))),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid privilege data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PrivilegeDTO> createPrivilege(@Valid @RequestBody PrivilegeDTO privilegeDTO) {
        logger.info("Creating new privilege: {}", privilegeDTO);
        Privilege savedPrivilege = privilegeService.savePrivilege(privilegeMapper.toEntity(privilegeDTO));
        return ResponseEntity.created(URI.create("/api/privileges/" + savedPrivilege.getId()))
                .body(privilegeMapper.toDto(savedPrivilege));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEACTIVATE_USER')")
    @Operation(summary = "Delete privilege", description = "Removes privilege from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Privilege deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Privilege not found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deletePrivilege(@PathVariable Long id) {
        logger.warn("Deleting privilege with id: {}", id);
        privilegeService.deletePrivilege(id);
        return ResponseEntity.noContent().build();
    }
}
