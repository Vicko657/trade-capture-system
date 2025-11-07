package com.technicalchallenge.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_privilege")
@IdClass(UserPrivilegeId.class)
@Schema(description = "Links user profiles to the assigned privilege")
public class UserPrivilege {
    @Id
    @Column(name = "userprofile_id")
    @Schema(description = "Unique Identifier of User profile", example = "1003L")
    private Long userId;

    @Id
    @Column(name = "privilege_id")
    @Schema(description = "Unique Identifier of Privilege", example = "1005L")
    private Long privilegeId;

    @ManyToOne
    @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    @Schema(description = "Privilege entity assigned to the user", implementation = Privilege.class)
    private Privilege privilege;

    @ManyToOne
    @JoinColumn(name = "userprofile_id", referencedColumnName = "id")
    @Schema(description = "User profile linked to the privilege", implementation = UserProfile.class)
    private UserProfile userProfile;
}
