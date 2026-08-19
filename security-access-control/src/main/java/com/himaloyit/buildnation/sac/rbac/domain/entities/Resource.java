package com.himaloyit.buildnation.sac.rbac.domain.entities;

import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import com.himaloyit.buildnation.sac.rbac.domain.enums.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "sac_resource")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Resource extends RbacAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID resourceId;

    @Column(nullable = false, unique = true)
    private String resourceCode;

    @Column(nullable = false)
    private String resourceName;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String apiPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod httpMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;
}
