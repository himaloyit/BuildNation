package com.himaloyit.buildnation.sac.rbac.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Entity
@Table(name = "sac_principal_role", uniqueConstraints = @UniqueConstraint(columnNames = {"principal_id", "role_id"}))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"principal", "role"})
@EqualsAndHashCode(callSuper = false)
public class PrincipalRole extends RbacAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID principalRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principal_id", nullable = false)
    private Principal principal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
