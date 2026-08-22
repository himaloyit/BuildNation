package com.himaloyit.buildnation.ui.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.mm.domain.model.UpdateMemberRoleRequest. */
@Data
@AllArgsConstructor
public class UpdateMemberRoleRequest {
    private MemberRole role;
}
