package com.himaloyit.buildnation.cdm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ICategoryRepository extends JpaRepository<Category, UUID> {
}
