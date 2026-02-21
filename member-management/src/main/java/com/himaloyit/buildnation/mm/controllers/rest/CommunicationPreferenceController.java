package com.himaloyit.buildnation.mm.controllers.rest;

import com.himaloyit.buildnation.mm.domain.dto.CommunicationPreferenceDTO;
import com.himaloyit.buildnation.mm.domain.model.ApiResponse;
import com.himaloyit.buildnation.mm.domain.model.UpdateCommunicationPreferenceRequest;
import com.himaloyit.buildnation.mm.services.iServices.ICommunicationPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@RestController
@RequestMapping("/api/v1/members")
public class CommunicationPreferenceController {

    private final ICommunicationPreferenceService iCommunicationPreferenceService;

    public CommunicationPreferenceController(ICommunicationPreferenceService iCommunicationPreferenceService) {
        this.iCommunicationPreferenceService = iCommunicationPreferenceService;
    }

    @GetMapping("/{id}/communication-preferences")
    public ResponseEntity<ApiResponse<CommunicationPreferenceDTO>> getPreferences(@PathVariable UUID id) {
        CommunicationPreferenceDTO prefs = iCommunicationPreferenceService.getPreferencesByMemberId(id);
        return ResponseEntity.ok(ApiResponse.success("Communication preferences retrieved", prefs));
    }

    @PutMapping("/{id}/communication-preferences")
    public ResponseEntity<ApiResponse<CommunicationPreferenceDTO>> updatePreferences(
            @PathVariable UUID id,
            @RequestBody UpdateCommunicationPreferenceRequest request) {
        CommunicationPreferenceDTO updated = iCommunicationPreferenceService.updatePreferences(id, request);
        return ResponseEntity.ok(ApiResponse.success("Communication preferences updated", updated));
    }
}
