package com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
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
public interface IRoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);

    @Query("SELECT r FROM Role r JOIN PrincipalRole pr ON pr.role = r JOIN pr.principal p " +
            "WHERE p.principalCode = :principalCode AND pr.status = com.himaloyit.buildnation.sac.rbac.domain.enums.Status.ACTIVE " +
            "AND r.status = com.himaloyit.buildnation.sac.rbac.domain.enums.Status.ACTIVE")
    List<Role> findActiveRolesByPrincipalCode(@Param("principalCode") String principalCode);
}
