package com.himaloyit.buildnation.mm.domain.model;

import com.himaloyit.buildnation.mm.domain.enums.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRoleRequest {

    @NotNull(message = "Role is mandatory")
    private MemberRole role;
}
