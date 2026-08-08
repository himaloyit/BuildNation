package com.himaloyit.buildnation.cdm.audit.services.impl;

import com.himaloyit.buildnation.cdm.audit.domain.dto.AuditLogDTO;
import com.himaloyit.buildnation.cdm.audit.domain.entities.AuditLog;
import com.himaloyit.buildnation.cdm.audit.domain.enums.AuditAction;
import com.himaloyit.buildnation.cdm.audit.domain.mapper.IAuditLogMapper;
import com.himaloyit.buildnation.cdm.audit.domain.repositories.iRepositories.IAuditLogRepository;
import com.himaloyit.buildnation.cdm.audit.services.iServices.IAuditService;
import com.himaloyit.buildnation.cdm.common.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AuditService implements IAuditService {

    private static final String DEFAULT_PERFORMED_BY = "system";

    private final IAuditLogRepository iAuditLogRepository;
    private final IAuditLogMapper iAuditLogMapper;

    public AuditService(IAuditLogRepository iAuditLogRepository, IAuditLogMapper iAuditLogMapper) {
        this.iAuditLogRepository = iAuditLogRepository;
        this.iAuditLogMapper = iAuditLogMapper;
    }

    @Override
    public AuditLogDTO logAudit(String entityType, UUID entityId, AuditAction action, String oldValue, String newValue, String performedBy) {
        log.info("Logging audit: entityType={}, entityId={}, action={}", entityType, entityId, action);
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .performedBy(StringUtils.hasText(performedBy) ? performedBy : DEFAULT_PERFORMED_BY)
                .performedAt(LocalDateTime.now())
                .build();

        AuditLog saved = iAuditLogRepository.save(auditLog);
        log.info("Audit logged: id={}", saved.getId());
        return iAuditLogMapper.toDto(saved);
    }

    @Override
    public AuditLogDTO getAuditLog(UUID id) {
        log.debug("Fetching audit log: id={}", id);
        return iAuditLogRepository.findById(id)
                .map(iAuditLogMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("AuditLog not found with id: " + id));
    }

    @Override
    public Page<AuditLogDTO> getAllAuditLogs(Pageable pageable) {
        return iAuditLogRepository.findAll(pageable).map(iAuditLogMapper::toDto);
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByEntity(String entityType, UUID entityId, Pageable pageable) {
        return iAuditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable).map(iAuditLogMapper::toDto);
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByEntityType(String entityType, Pageable pageable) {
        return iAuditLogRepository.findByEntityType(entityType, pageable).map(iAuditLogMapper::toDto);
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByPerformedBy(String performedBy, Pageable pageable) {
        return iAuditLogRepository.findByPerformedBy(performedBy, pageable).map(iAuditLogMapper::toDto);
    }
}
