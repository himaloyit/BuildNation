package com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.region.domain.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IDistrictRepository extends JpaRepository<District, UUID> {
}
