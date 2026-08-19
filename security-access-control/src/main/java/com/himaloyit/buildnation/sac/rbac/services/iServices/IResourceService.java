package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.ResourceDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreateResourceRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdateResourceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IResourceService {

    ResourceDTO createResource(CreateResourceRequest request);

    ResourceDTO getResource(UUID id);

    Page<ResourceDTO> getAllResources(Pageable pageable);

    Page<ResourceDTO> getResourcesByServiceName(String serviceName, Pageable pageable);

    ResourceDTO updateResource(UUID id, UpdateResourceRequest request);

    void deleteResource(UUID id);
}
