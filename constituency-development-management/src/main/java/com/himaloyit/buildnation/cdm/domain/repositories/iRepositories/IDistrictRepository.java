package com.himaloyit.buildnation.cdm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.domain.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IDistrictRepository extends JpaRepository<District, UUID> {
}
