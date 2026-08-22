package com.himaloyit.buildnation.ui.dto.prj;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.model.UpdateProjectPriorityRequest. */
@Data
@AllArgsConstructor
public class UpdateProjectPriorityRequest {
    private Integer priorityScore;
}
