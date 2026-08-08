package com.himaloyit.buildnation.cdm.prj.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.prj.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ICategoryRepository extends JpaRepository<Category, UUID> {
}
