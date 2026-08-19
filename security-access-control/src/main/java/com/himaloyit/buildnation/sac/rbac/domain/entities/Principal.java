package com.himaloyit.buildnation.sac.rbac.domain.entities;

import com.himaloyit.buildnation.sac.rbac.domain.enums.CredentialType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Sole identity table for every caller type (human or machine) — fully replaces the
 * old User/UserRole. See Doc/Prompt/Prompt-1 §1.
 */

@Entity
@Table(name = "sac_principal")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true, exclude = "password")
@EqualsAndHashCode(callSuper = false)
public class Principal extends RbacAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID principalId;

    @Column(nullable = false, unique = true)
    private String principalCode;

    @Column(nullable = false)
    private String principalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrincipalType principalType;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CredentialType credentialType;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean accountNonLocked = true;

    private LocalDateTime expiresAt;

    private LocalDateTime lastLoginAt;

    private String ownerContact;

    private String description;
}
