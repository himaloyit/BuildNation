package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.ProjectDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Category;
import com.himaloyit.buildnation.cdm.domain.entities.Project;
import com.himaloyit.buildnation.cdm.domain.entities.SubCategory;
import com.himaloyit.buildnation.cdm.domain.entities.Village;
import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.domain.mapper.IProjectMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateProjectRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectPriorityRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectStatusRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.ICategoryRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IProjectRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.ISubCategoryRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IVillageRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IProjectService;
import com.himaloyit.buildnation.cdm.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class ProjectService implements IProjectService {

    private static final Set<ProjectStatus> QUEUE_STATUSES = Set.of(
            ProjectStatus.NEW, ProjectStatus.PENDING_APPROVAL, ProjectStatus.APPROVED
    );

    private final IProjectRepository iProjectRepository;
    private final ICategoryRepository iCategoryRepository;
    private final ISubCategoryRepository iSubCategoryRepository;
    private final IVillageRepository iVillageRepository;
    private final IProjectMapper iProjectMapper;

    public ProjectService(IProjectRepository iProjectRepository, ICategoryRepository iCategoryRepository,
                           ISubCategoryRepository iSubCategoryRepository, IVillageRepository iVillageRepository,
                           IProjectMapper iProjectMapper) {
        this.iProjectRepository = iProjectRepository;
        this.iCategoryRepository = iCategoryRepository;
        this.iSubCategoryRepository = iSubCategoryRepository;
        this.iVillageRepository = iVillageRepository;
        this.iProjectMapper = iProjectMapper;
    }

    @Override
    @CacheEvict(value = "projects-list", allEntries = true)
    public ProjectDTO createProject(CreateProjectRequest request) {
        log.info("Creating project: name={}, categoryId={}", request.getName(), request.getCategoryId());
        Category category = iCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
        SubCategory subCategory = iSubCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id: " + request.getSubCategoryId()));
        Village village = iVillageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new EntityNotFoundException("Village not found with id: " + request.getVillageId()));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .currentCondition(request.getCurrentCondition())
                .category(category)
                .subCategory(subCategory)
                .village(village)
                .estimatedCost(request.getEstimatedCost())
                .priorityScore(request.getPriorityScore() != null ? request.getPriorityScore() : 0)
                .submittedBy(request.getSubmittedBy())
                .status(ProjectStatus.NEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Project saved = iProjectRepository.save(project);
        log.info("Project created: id={}", saved.getId());
        return iProjectMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "projects", key = "#id")
    public ProjectDTO getProject(UUID id) {
        log.debug("Fetching project: id={}", id);
        return iProjectRepository.findById(id)
                .map(iProjectMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    @Override
    @Cacheable(value = "projects-list", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProjectDTO> getAllProjects(Pageable pageable) {
        return iProjectRepository.findAll(pageable).map(iProjectMapper::toDto);
    }

    @Override
    public Page<ProjectDTO> getProjectsByCategory(UUID categoryId, Pageable pageable) {
        return iProjectRepository.findByCategoryId(categoryId, pageable).map(iProjectMapper::toDto);
    }

    @Override
    public Page<ProjectDTO> getProjectsByStatus(ProjectStatus status, Pageable pageable) {
        return iProjectRepository.findByStatus(status, pageable).map(iProjectMapper::toDto);
    }

    @Override
    @Caching(
        put  = @CachePut(value = "projects", key = "#id"),
        evict = @CacheEvict(value = "projects-list", allEntries = true)
    )
    public ProjectDTO updateProject(UUID id, UpdateProjectRequest request) {
        log.info("Updating project: id={}", id);
        Project project = iProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getCurrentCondition() != null) project.setCurrentCondition(request.getCurrentCondition());
        if (request.getEstimatedCost() != null) project.setEstimatedCost(request.getEstimatedCost());
        if (request.getSubmittedBy() != null) project.setSubmittedBy(request.getSubmittedBy());
        if (request.getCategoryId() != null) {
            Category category = iCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            project.setCategory(category);
        }
        if (request.getSubCategoryId() != null) {
            SubCategory subCategory = iSubCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id: " + request.getSubCategoryId()));
            project.setSubCategory(subCategory);
        }
        if (request.getVillageId() != null) {
            Village village = iVillageRepository.findById(request.getVillageId())
                    .orElseThrow(() -> new EntityNotFoundException("Village not found with id: " + request.getVillageId()));
            project.setVillage(village);
        }
        project.setUpdatedAt(LocalDateTime.now());

        ProjectDTO updated = iProjectMapper.toDto(iProjectRepository.save(project));
        log.info("Project updated: id={}", id);
        return updated;
    }

    @Override
    @Caching(
        put  = @CachePut(value = "projects", key = "#id"),
        evict = @CacheEvict(value = "projects-list", allEntries = true)
    )
    public ProjectDTO updateProjectStatus(UUID id, UpdateProjectStatusRequest request) {
        log.info("Updating project status: id={}, status={}", id, request.getStatus());
        Project project = iProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        project.setStatus(request.getStatus());
        project.setUpdatedAt(LocalDateTime.now());

        return iProjectMapper.toDto(iProjectRepository.save(project));
    }

    @Override
    @Caching(
        put  = @CachePut(value = "projects", key = "#id"),
        evict = @CacheEvict(value = "projects-list", allEntries = true)
    )
    public ProjectDTO updateProjectPriority(UUID id, UpdateProjectPriorityRequest request) {
        log.info("Updating project priority score: id={}, score={}", id, request.getPriorityScore());
        Project project = iProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        project.setPriorityScore(request.getPriorityScore());
        project.setUpdatedAt(LocalDateTime.now());

        return iProjectMapper.toDto(iProjectRepository.save(project));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "projects", key = "#id"),
        @CacheEvict(value = "projects-list", allEntries = true)
    })
    public void deleteProject(UUID id) {
        log.info("Deleting project: id={}", id);
        if (!iProjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found with id: " + id);
        }
        iProjectRepository.deleteById(id);
        log.info("Project deleted: id={}", id);
    }

    @Override
    public Page<ProjectDTO> getPriorityQueue(UUID categoryId, Pageable pageable) {
        Page<Project> projects = categoryId != null
                ? iProjectRepository.findByCategoryIdOrderByPriorityRankAscIdAsc(categoryId, pageable)
                : iProjectRepository.findAllByOrderByPriorityRankAscIdAsc(pageable);
        return projects.map(iProjectMapper::toDto);
    }

    @Override
    @CacheEvict(value = {"projects", "projects-list"}, allEntries = true)
    public int recalculatePriorityQueue(UUID categoryId) {
        log.info("Recalculating priority queue: categoryId={}", categoryId);
        List<Project> queued = categoryId != null
                ? iProjectRepository.findByStatusInAndCategoryIdOrderByPriorityScoreDesc(QUEUE_STATUSES, categoryId)
                : iProjectRepository.findByStatusInOrderByPriorityScoreDesc(QUEUE_STATUSES);

        int rank = 1;
        for (Project project : queued) {
            project.setPriorityRank(rank++);
            project.setUpdatedAt(LocalDateTime.now());
        }
        iProjectRepository.saveAll(queued);
        log.info("Priority queue recalculated: {} projects ranked", queued.size());
        return queued.size();
    }
}
