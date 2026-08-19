package com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Repository
public interface IPermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    Page<Permission> findByResource_ResourceId(UUID resourceId, Pageable pageable);

    @Query("SELECT DISTINCT perm FROM Permission perm JOIN FETCH perm.resource " +
            "JOIN RolePermission rp ON rp.permission = perm " +
            "JOIN rp.role r JOIN PrincipalRole pr ON pr.role = r JOIN pr.principal p " +
            "WHERE p.principalCode = :principalCode " +
            "AND pr.status = com.himaloyit.buildnation.sac.rbac.domain.enums.Status.ACTIVE " +
            "AND rp.status = com.himaloyit.buildnation.sac.rbac.domain.enums.Status.ACTIVE " +
            "AND r.status = com.himaloyit.buildnation.sac.rbac.domain.enums.Status.ACTIVE " +
            "AND perm.status = com.himaloyit.buildnation.sac.rbac.domain.enums.Status.ACTIVE")
    List<Permission> findActivePermissionsByPrincipalCode(@Param("principalCode") String principalCode);
}
