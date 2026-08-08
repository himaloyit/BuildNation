package com.himaloyit.buildnation.cdm.fund.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.fund.domain.entities.Fund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IFundRepository extends JpaRepository<Fund, UUID> {

    Page<Fund> findByCategoryId(UUID categoryId, Pageable pageable);
}
