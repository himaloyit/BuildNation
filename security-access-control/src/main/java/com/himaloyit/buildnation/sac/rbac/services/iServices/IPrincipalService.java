package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreatePrincipalRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePrincipalRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePrincipalStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IPrincipalService {

    PrincipalDTO createPrincipal(CreatePrincipalRequest request);

    PrincipalDTO getPrincipal(UUID id);

    PrincipalDTO getPrincipalByCode(String principalCode);

    Page<PrincipalDTO> getAllPrincipals(Pageable pageable);

    Page<PrincipalDTO> getPrincipalsByType(PrincipalType type, Pageable pageable);

    PrincipalDTO updatePrincipal(UUID id, UpdatePrincipalRequest request);

    PrincipalDTO updatePrincipalStatus(UUID id, UpdatePrincipalStatusRequest request);

    void deletePrincipal(UUID id);
}
