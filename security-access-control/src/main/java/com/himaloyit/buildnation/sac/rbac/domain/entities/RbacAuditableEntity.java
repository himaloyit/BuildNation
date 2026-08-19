package com.himaloyit.buildnation.sac.rbac.domain.entities;

import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Shared audit/optimistic-locking columns for every RBAC entity (Principal, Role,
 * Resource, Permission, PrincipalRole, RolePermission) per Prompt-1 §1/§8 — new
 * pattern for this codebase, scoped to the rbac package only.
 */

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class RbacAuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String createdBy;

    private LocalDateTime createdDate;

    private String updatedBy;

    private LocalDateTime updatedDate;

    @Version
    private Long version;
}
