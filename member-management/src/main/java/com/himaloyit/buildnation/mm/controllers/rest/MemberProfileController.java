package com.himaloyit.buildnation.mm.controllers.rest;

import com.himaloyit.buildnation.mm.domain.dto.MemberProfileDTO;
import com.himaloyit.buildnation.mm.domain.model.ApiResponse;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberProfileRequest;
import com.himaloyit.buildnation.mm.services.iServices.IMemberProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@RestController
@RequestMapping("/api/v1/members")
public class MemberProfileController {

    private final IMemberProfileService iMemberProfileService;

    public MemberProfileController(IMemberProfileService iMemberProfileService) {
        this.iMemberProfileService = iMemberProfileService;
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<MemberProfileDTO>> getProfile(@PathVariable UUID id) {
        MemberProfileDTO profile = iMemberProfileService.getProfileByMemberId(id);
        return ResponseEntity.ok(ApiResponse.success("Member profile retrieved", profile));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<MemberProfileDTO>> updateProfile(
            @PathVariable UUID id,
            @RequestBody UpdateMemberProfileRequest request) {
        MemberProfileDTO updated = iMemberProfileService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Member profile updated", updated));
    }
}
