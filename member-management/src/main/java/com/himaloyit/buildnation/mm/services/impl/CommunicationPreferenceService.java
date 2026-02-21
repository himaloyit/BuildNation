package com.himaloyit.buildnation.mm.services.impl;

import com.himaloyit.buildnation.mm.domain.dto.CommunicationPreferenceDTO;
import com.himaloyit.buildnation.mm.domain.entities.CommunicationPreference;
import com.himaloyit.buildnation.mm.domain.entities.Member;
import com.himaloyit.buildnation.mm.domain.mapper.ICommunicationPreferenceMapper;
import com.himaloyit.buildnation.mm.domain.model.UpdateCommunicationPreferenceRequest;
import com.himaloyit.buildnation.mm.domain.repositories.iRepositories.IMemberRepository;
import com.himaloyit.buildnation.mm.services.iServices.ICommunicationPreferenceService;
import com.himaloyit.buildnation.mm.util.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Service
public class CommunicationPreferenceService implements ICommunicationPreferenceService {

    private final IMemberRepository iMemberRepository;
    private final ICommunicationPreferenceMapper iCommunicationPreferenceMapper;

    public CommunicationPreferenceService(IMemberRepository iMemberRepository,
                                          ICommunicationPreferenceMapper iCommunicationPreferenceMapper) {
        this.iMemberRepository = iMemberRepository;
        this.iCommunicationPreferenceMapper = iCommunicationPreferenceMapper;
    }

    @Override
    @Transactional
    public CommunicationPreferenceDTO getPreferencesByMemberId(UUID memberId) {
        Member member = iMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));
        CommunicationPreference preference = member.getCommunicationPreference();
        if (preference == null) {
            throw new EntityNotFoundException("Communication preferences not found for member id: " + memberId);
        }
        return iCommunicationPreferenceMapper.toDto(preference);
    }

    @Override
    @Transactional
    public CommunicationPreferenceDTO updatePreferences(UUID memberId, UpdateCommunicationPreferenceRequest request) {
        Member member = iMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        CommunicationPreference preference = member.getCommunicationPreference();
        if (preference == null) {
            preference = new CommunicationPreference();
            preference.setCreatedAt(LocalDateTime.now());
            member.setCommunicationPreference(preference);
        }

        preference.setPreferEmail(request.isPreferEmail());
        preference.setPreferSms(request.isPreferSms());
        preference.setPreferWhatsApp(request.isPreferWhatsApp());
        preference.setPreferPhone(request.isPreferPhone());
        preference.setUpdatedAt(LocalDateTime.now());

        iMemberRepository.save(member);
        return iCommunicationPreferenceMapper.toDto(preference);
    }
}
