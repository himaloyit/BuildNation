package com.himaloyit.buildnation.ui.dto.prj;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.model.UpdateProjectStatusRequest. */
@Data
@AllArgsConstructor
public class UpdateProjectStatusRequest {
    private ProjectStatus status;
}
