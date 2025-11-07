package com.technicalchallenge.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user_profile")
@Schema(description = "Represents the user's role in the system")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique Identifier", example = "1000L")
    private Long id;

    @Schema(description = "User's assigned role", example = "SALES")
    private String userType;

    @Schema(description = "List of privileges linked the user profile", implementation = UserPrivilege.class)
    @OneToMany(mappedBy = "userProfile", fetch = FetchType.EAGER)
    private List<UserPrivilege> privileges;

}
