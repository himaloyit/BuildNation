package com.himaloyit.buildnation.mm.domain.dto;

import com.himaloyit.buildnation.mm.domain.enums.MemberRole;
import com.himaloyit.buildnation.mm.domain.enums.MemberStatus;
import lombok.*;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
