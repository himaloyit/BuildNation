package com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Repository
public interface IPrincipalRepository extends JpaRepository<Principal, UUID> {

    Optional<Principal> findByPrincipalCode(String principalCode);

    boolean existsByPrincipalCode(String principalCode);

    Optional<Principal> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Principal> findByPrincipalType(PrincipalType principalType, Pageable pageable);
}
