package com.technicalchallenge.controller;

import com.technicalchallenge.service.AuthorizationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@Validated
@AllArgsConstructor
@Tag(name = "Authorization", description = "Authorization of roles for users")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/{userName}")
    public ResponseEntity<?> login(@PathVariable(name = "userName") String userName,
            @RequestParam(name = "Authorization") String authorization) {

        return authorizationService.authenticateUser(userName, authorization) ? ResponseEntity.ok("Login successful")
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login failed");
    }
}