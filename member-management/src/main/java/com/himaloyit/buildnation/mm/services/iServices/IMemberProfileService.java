package com.himaloyit.buildnation.mm.services.iServices;

import com.himaloyit.buildnation.mm.domain.dto.MemberProfileDTO;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberProfileRequest;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IMemberProfileService {

    MemberProfileDTO getProfileByMemberId(UUID memberId);

    MemberProfileDTO updateProfile(UUID memberId, UpdateMemberProfileRequest request);
}
