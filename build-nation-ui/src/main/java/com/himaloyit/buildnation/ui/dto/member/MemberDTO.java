package com.himaloyit.buildnation.ui.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.mm.domain.dto.MemberDTO. */
@Data
@NoArgsConstructor
public class MemberDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String position;
    private MemberRole role;
    private MemberStatus status;
    private UUID constituencyId;
}
