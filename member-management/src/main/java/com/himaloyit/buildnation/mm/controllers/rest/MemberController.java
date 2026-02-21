package com.himaloyit.buildnation.mm.controllers.rest;

import com.himaloyit.buildnation.mm.domain.dto.MemberDTO;
import com.himaloyit.buildnation.mm.domain.enums.MemberRole;
import com.himaloyit.buildnation.mm.domain.enums.MemberStatus;
import com.himaloyit.buildnation.mm.domain.model.ApiResponse;
import com.himaloyit.buildnation.mm.domain.model.CreateMemberRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberRoleRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberStatusRequest;
import com.himaloyit.buildnation.mm.services.iServices.IMemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final IMemberService iMemberService;

    public MemberController(IMemberService iMemberService) {
        this.iMemberService = iMemberService;
    }

    // Create
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MemberDTO>> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberDTO saved = iMemberService.createMember(request);
        return ResponseEntity.ok(ApiResponse.success("Member created successfully", saved));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDTO>> getMemberById(@PathVariable UUID id) {
        MemberDTO member = iMemberService.getMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member found", member));
    }

    // Get All (paginated)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MemberDTO>>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MemberDTO> pagedMembers = iMemberService.getAllMembers(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged members retrieved", pagedMembers));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMember(
            @PathVariable UUID id,
            @RequestBody UpdateMemberRequest request) {
        MemberDTO updated = iMemberService.updateMember(id, request);
        return ResponseEntity.ok(ApiResponse.success("Member updated successfully", updated));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable UUID id) {
        iMemberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", null));
    }

    // Update role
    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMemberRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        MemberDTO updated = iMemberService.updateMemberRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Member role updated", updated));
    }

    // Get by role (paginated)
    @GetMapping("/by-role/{role}")
    public ResponseEntity<ApiResponse<Page<MemberDTO>>> getMembersByRole(
            @PathVariable MemberRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MemberDTO> members = iMemberService.getMembersByRole(role, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Members by role retrieved", members));
    }

    // Update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMemberStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberStatusRequest request) {
        MemberDTO updated = iMemberService.updateMemberStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Member status updated", updated));
    }

    // Get by status (paginated)
    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<Page<MemberDTO>>> getMembersByStatus(
            @PathVariable MemberStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MemberDTO> members = iMemberService.getMembersByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Members by status retrieved", members));
    }
}
