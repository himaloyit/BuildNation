package com.himaloyit.buildnation.cdm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.domain.entities.Union;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUnionRepository extends JpaRepository<Union, UUID> {

    Page<Union> findByUpazilaId(UUID upazilaId, Pageable pageable);
}
