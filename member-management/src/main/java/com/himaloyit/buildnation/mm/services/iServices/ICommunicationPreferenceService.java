package com.himaloyit.buildnation.mm.services.iServices;

import com.himaloyit.buildnation.mm.domain.dto.CommunicationPreferenceDTO;
import com.himaloyit.buildnation.mm.domain.model.UpdateCommunicationPreferenceRequest;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface ICommunicationPreferenceService {

    CommunicationPreferenceDTO getPreferencesByMemberId(UUID memberId);

    CommunicationPreferenceDTO updatePreferences(UUID memberId, UpdateCommunicationPreferenceRequest request);
}
