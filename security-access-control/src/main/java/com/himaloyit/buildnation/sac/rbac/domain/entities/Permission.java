package com.himaloyit.buildnation.sac.rbac.domain.entities;

import com.himaloyit.buildnation.sac.rbac.domain.enums.Action;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "sac_permission")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true, exclude = "resource")
@EqualsAndHashCode(callSuper = false)
public class Permission extends RbacAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID permissionId;

    @Column(nullable = false, unique = true)
    private String permissionCode;

    @Column(nullable = false)
    private String permissionName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
}
