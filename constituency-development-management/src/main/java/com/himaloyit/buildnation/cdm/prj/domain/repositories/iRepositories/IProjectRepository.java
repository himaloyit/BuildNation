package com.himaloyit.buildnation.cdm.prj.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.prj.domain.entities.Project;
import com.himaloyit.buildnation.cdm.prj.domain.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByCategoryId(UUID categoryId, Pageable pageable);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByCategoryIdOrderByPriorityRankAscIdAsc(UUID categoryId, Pageable pageable);

    Page<Project> findAllByOrderByPriorityRankAscIdAsc(Pageable pageable);

    List<Project> findByStatusInAndCategoryIdOrderByPriorityScoreDesc(Collection<ProjectStatus> statuses, UUID categoryId);

    List<Project> findByStatusInOrderByPriorityScoreDesc(Collection<ProjectStatus> statuses);
}
