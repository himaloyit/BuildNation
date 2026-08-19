package com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories;

import com.himaloyit.buildnation.sac.rbac.domain.entities.PrincipalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Repository
public interface IPrincipalRoleRepository extends JpaRepository<PrincipalRole, UUID> {

    boolean existsByPrincipal_PrincipalIdAndRole_RoleId(UUID principalId, UUID roleId);

    Optional<PrincipalRole> findByPrincipal_PrincipalIdAndRole_RoleId(UUID principalId, UUID roleId);

    List<PrincipalRole> findByPrincipal_PrincipalId(UUID principalId);

    List<PrincipalRole> findByRole_RoleId(UUID roleId);
}
