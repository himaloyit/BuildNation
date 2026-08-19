package com.himaloyit.buildnation.sac.rbac.domain.model;

import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrincipalStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;
}
