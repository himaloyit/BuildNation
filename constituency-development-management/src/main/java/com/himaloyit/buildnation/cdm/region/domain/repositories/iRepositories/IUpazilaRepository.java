package com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.region.domain.entities.Upazila;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUpazilaRepository extends JpaRepository<Upazila, UUID> {

    Page<Upazila> findByDistrictId(UUID districtId, Pageable pageable);
}
