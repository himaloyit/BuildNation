package com.himaloyit.buildnation.mm.services.iServices;

import com.himaloyit.buildnation.mm.domain.dto.MemberDTO;
import com.himaloyit.buildnation.mm.domain.enums.MemberRole;
import com.himaloyit.buildnation.mm.domain.enums.MemberStatus;
import com.himaloyit.buildnation.mm.domain.model.CreateMemberRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberRoleRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IMemberService {

    MemberDTO createMember(CreateMemberRequest request);
    MemberDTO getMember(UUID id);
    List<MemberDTO> getAllMembers();
    Page<MemberDTO> getAllMembers(Pageable pageable);
    MemberDTO updateMember(UUID id, UpdateMemberRequest request);
    void deleteMember(UUID id);

    MemberDTO updateMemberRole(UUID id, UpdateMemberRoleRequest request);
    Page<MemberDTO> getMembersByRole(MemberRole role, Pageable pageable);
    MemberDTO updateMemberStatus(UUID id, UpdateMemberStatusRequest request);
    Page<MemberDTO> getMembersByStatus(MemberStatus status, Pageable pageable);
}
