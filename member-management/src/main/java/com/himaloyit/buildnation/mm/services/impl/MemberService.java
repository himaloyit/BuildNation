package com.himaloyit.buildnation.mm.services.impl;

import com.himaloyit.buildnation.mm.domain.dto.MemberDTO;
import com.himaloyit.buildnation.mm.domain.entities.CommunicationPreference;
import com.himaloyit.buildnation.mm.domain.entities.Member;
import com.himaloyit.buildnation.mm.domain.entities.MemberProfile;
import com.himaloyit.buildnation.mm.domain.enums.MemberRole;
import com.himaloyit.buildnation.mm.domain.enums.MemberStatus;
import com.himaloyit.buildnation.mm.domain.mapper.IMemberMapper;
import com.himaloyit.buildnation.mm.domain.model.CreateMemberRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberRoleRequest;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberStatusRequest;
import com.himaloyit.buildnation.mm.domain.repositories.iRepositories.IMemberRepository;
import com.himaloyit.buildnation.mm.services.iServices.IMemberService;
import com.himaloyit.buildnation.mm.util.exceptions.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * Author: Rajib Kumer Ghosh
 */

@Service
public class MemberService implements IMemberService {

    private final IMemberRepository iMemberRepository;
    private final IMemberMapper iMemberMapper;

    public MemberService(IMemberRepository iMemberRepository, IMemberMapper iMemberMapper) {
        this.iMemberRepository = iMemberRepository;
        this.iMemberMapper = iMemberMapper;
    }

    @Override
    @CacheEvict(value = "members-list", allEntries = true)
    public MemberDTO createMember(CreateMemberRequest request) {
        MemberProfile profile = MemberProfile.builder()
                .dob(request.getDob() != null ? request.getDob().toString() : null)
                .gender(request.getGender())
                .streetAddress(request.getAddress())
                .build();

        CommunicationPreference preference = CommunicationPreference.builder()
                .preferEmail(false)
                .preferSms(false)
                .preferWhatsApp(false)
                .preferPhone(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MemberRole role = null;
        if (request.getRole() != null) {
            try {
                role = MemberRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                role = MemberRole.GENERAL_MEMBER;
            }
        }

        Member member = Member.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .constituencyId(request.getConstituencyId())
                .role(role != null ? role : MemberRole.GENERAL_MEMBER)
                .status(MemberStatus.ACTIVE)
                .profile(profile)
                .communicationPreference(preference)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Member saved = iMemberRepository.save(member);
        return iMemberMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "members", key = "#id")
    public MemberDTO getMember(UUID id) {
        return iMemberRepository.findById(id)
                .map(iMemberMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        return iMemberRepository.findAll().stream()
                .map(iMemberMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "members-list", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        return iMemberRepository.findAll(pageable).map(iMemberMapper::toDto);
    }

    @Override
    @Caching(
        put  = @CachePut(value = "members", key = "#id"),
        evict = @CacheEvict(value = "members-list", allEntries = true)
    )
    public MemberDTO updateMember(UUID id, UpdateMemberRequest request) {
        Member member = iMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));

        if (request.getFullName() != null) member.setFullName(request.getFullName());
        if (request.getPhone() != null) member.setPhone(request.getPhone());
        if (request.getPosition() != null) member.setPosition(request.getPosition());
        if (request.getConstituencyId() != null) member.setConstituencyId(request.getConstituencyId());
        member.setUpdatedAt(LocalDateTime.now());

        return iMemberMapper.toDto(iMemberRepository.save(member));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "members", key = "#id"),
        @CacheEvict(value = "members-list", allEntries = true)
    })
    public void deleteMember(UUID id) {
        if (!iMemberRepository.existsById(id)) {
            throw new EntityNotFoundException("Member not found with id: " + id);
        }
        iMemberRepository.deleteById(id);
    }

    @Override
    @Caching(
        put  = @CachePut(value = "members", key = "#id"),
        evict = @CacheEvict(value = "members-list", allEntries = true)
    )
    public MemberDTO updateMemberRole(UUID id, UpdateMemberRoleRequest request) {
        Member member = iMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
        member.setRole(request.getRole());
        member.setUpdatedAt(LocalDateTime.now());
        return iMemberMapper.toDto(iMemberRepository.save(member));
    }

    @Override
    public Page<MemberDTO> getMembersByRole(MemberRole role, Pageable pageable) {
        return iMemberRepository.findByRole(role, pageable).map(iMemberMapper::toDto);
    }

    @Override
    @Caching(
        put  = @CachePut(value = "members", key = "#id"),
        evict = @CacheEvict(value = "members-list", allEntries = true)
    )
    public MemberDTO updateMemberStatus(UUID id, UpdateMemberStatusRequest request) {
        Member member = iMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
        member.setStatus(request.getStatus());
        member.setUpdatedAt(LocalDateTime.now());
        return iMemberMapper.toDto(iMemberRepository.save(member));
    }

    @Override
    public Page<MemberDTO> getMembersByStatus(MemberStatus status, Pageable pageable) {
        return iMemberRepository.findByStatus(status, pageable).map(iMemberMapper::toDto);
    }
}
