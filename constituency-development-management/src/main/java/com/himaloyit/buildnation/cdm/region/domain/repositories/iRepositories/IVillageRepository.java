package com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.region.domain.entities.Village;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IVillageRepository extends JpaRepository<Village, UUID> {

    Page<Village> findByWardId(UUID wardId, Pageable pageable);
}
