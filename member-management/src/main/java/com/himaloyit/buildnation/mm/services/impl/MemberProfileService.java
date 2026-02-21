package com.himaloyit.buildnation.mm.services.impl;

import com.himaloyit.buildnation.mm.domain.dto.MemberProfileDTO;
import com.himaloyit.buildnation.mm.domain.entities.Member;
import com.himaloyit.buildnation.mm.domain.entities.MemberProfile;
import com.himaloyit.buildnation.mm.domain.mapper.IMemberProfileMapper;
import com.himaloyit.buildnation.mm.domain.model.UpdateMemberProfileRequest;
import com.himaloyit.buildnation.mm.domain.repositories.iRepositories.IMemberRepository;
import com.himaloyit.buildnation.mm.services.iServices.IMemberProfileService;
import com.himaloyit.buildnation.mm.util.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Service
public class MemberProfileService implements IMemberProfileService {

    private final IMemberRepository iMemberRepository;
    private final IMemberProfileMapper iMemberProfileMapper;

    public MemberProfileService(IMemberRepository iMemberRepository, IMemberProfileMapper iMemberProfileMapper) {
        this.iMemberRepository = iMemberRepository;
        this.iMemberProfileMapper = iMemberProfileMapper;
    }

    @Override
    @Transactional
    public MemberProfileDTO getProfileByMemberId(UUID memberId) {
        Member member = iMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));
        MemberProfile profile = member.getProfile();
        if (profile == null) {
            throw new EntityNotFoundException("Profile not found for member id: " + memberId);
        }
        return iMemberProfileMapper.toDto(profile);
    }

    @Override
    @Transactional
    public MemberProfileDTO updateProfile(UUID memberId, UpdateMemberProfileRequest request) {
        Member member = iMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        MemberProfile profile = member.getProfile();
        if (profile == null) {
            profile = new MemberProfile();
            member.setProfile(profile);
        }

        if (request.getDob() != null) profile.setDob(request.getDob());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getNationality() != null) profile.setNationality(request.getNationality());
        if (request.getStreetAddress() != null) profile.setStreetAddress(request.getStreetAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getState() != null) profile.setState(request.getState());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getPostalCode() != null) profile.setPostalCode(request.getPostalCode());
        if (request.getFacebookUrl() != null) profile.setFacebookUrl(request.getFacebookUrl());
        if (request.getTwitterUrl() != null) profile.setTwitterUrl(request.getTwitterUrl());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getInstagramUrl() != null) profile.setInstagramUrl(request.getInstagramUrl());
        if (request.getWebsiteUrl() != null) profile.setWebsiteUrl(request.getWebsiteUrl());

        iMemberRepository.save(member);
        return iMemberProfileMapper.toDto(profile);
    }
}
