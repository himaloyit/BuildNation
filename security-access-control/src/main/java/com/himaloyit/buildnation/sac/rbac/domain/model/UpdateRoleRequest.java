package com.himaloyit.buildnation.sac.rbac.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {

    private String roleName;
    private String description;
}
