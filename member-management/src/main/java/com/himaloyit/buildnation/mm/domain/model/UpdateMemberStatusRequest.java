package com.himaloyit.buildnation.mm.domain.model;

import com.himaloyit.buildnation.mm.domain.enums.MemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberStatusRequest {

    @NotNull(message = "Status is mandatory")
    private MemberStatus status;
}
