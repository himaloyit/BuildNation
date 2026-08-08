package com.himaloyit.buildnation.cdm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.domain.entities.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    Page<SubCategory> findByCategoryId(UUID categoryId, Pageable pageable);
}
