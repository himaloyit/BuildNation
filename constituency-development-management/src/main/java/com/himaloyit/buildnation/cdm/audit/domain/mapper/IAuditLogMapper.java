package com.himaloyit.buildnation.cdm.audit.domain.mapper;

import com.himaloyit.buildnation.cdm.audit.domain.dto.AuditLogDTO;
import com.himaloyit.buildnation.cdm.audit.domain.entities.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IAuditLogMapper {

    AuditLogDTO toDto(AuditLog auditLog);

    AuditLog toEntity(AuditLogDTO auditLogDTO);
}
