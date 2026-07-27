package com.himaloyit.buildnation.cdm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.domain.entities.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IWardRepository extends JpaRepository<Ward, UUID> {

    Page<Ward> findByUnionId(UUID unionId, Pageable pageable);
}
