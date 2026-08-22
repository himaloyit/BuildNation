package com.himaloyit.buildnation.ui.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.mm.domain.model.UpdateMemberRequest. */
@Data
@AllArgsConstructor
public class UpdateMemberRequest {
    private String fullName;
    private String phone;
    private String position;
    private UUID constituencyId;
}
