package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.ProjectDTO;
import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.domain.model.CreateProjectRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectPriorityRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IProjectService {

    ProjectDTO createProject(CreateProjectRequest request);
    ProjectDTO getProject(UUID id);
    Page<ProjectDTO> getAllProjects(Pageable pageable);
    Page<ProjectDTO> getProjectsByCategory(UUID categoryId, Pageable pageable);
    Page<ProjectDTO> getProjectsByStatus(ProjectStatus status, Pageable pageable);
    ProjectDTO updateProject(UUID id, UpdateProjectRequest request);
    ProjectDTO updateProjectStatus(UUID id, UpdateProjectStatusRequest request);
    ProjectDTO updateProjectPriority(UUID id, UpdateProjectPriorityRequest request);
    void deleteProject(UUID id);

    Page<ProjectDTO> getPriorityQueue(UUID categoryId, Pageable pageable);
    int recalculatePriorityQueue(UUID categoryId);
}
