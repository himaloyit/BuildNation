package com.himaloyit.buildnation.mm.domain.model;

import lombok.*;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRequest {
    private String fullName;
    private String phone;
    private String position;
    private UUID constituencyId;
}
