package com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Resource;
import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Repository
public interface IResourceRepository extends JpaRepository<Resource, UUID> {

    Optional<Resource> findByResourceCode(String resourceCode);

    boolean existsByResourceCode(String resourceCode);

    Page<Resource> findByServiceName(String serviceName, Pageable pageable);

    List<Resource> findByApiPathAndHttpMethod(String apiPath, HttpMethod httpMethod);
}
