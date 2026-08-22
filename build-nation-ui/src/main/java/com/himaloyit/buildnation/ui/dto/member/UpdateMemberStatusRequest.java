package com.himaloyit.buildnation.ui.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.mm.domain.model.UpdateMemberStatusRequest. */
@Data
@AllArgsConstructor
public class UpdateMemberStatusRequest {
    private MemberStatus status;
}
