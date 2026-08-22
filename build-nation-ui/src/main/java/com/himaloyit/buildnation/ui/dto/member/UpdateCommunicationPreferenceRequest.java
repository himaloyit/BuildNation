package com.himaloyit.buildnation.ui.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.mm.domain.model.UpdateCommunicationPreferenceRequest. */
@Data
@AllArgsConstructor
public class UpdateCommunicationPreferenceRequest {
    private boolean preferEmail;
    private boolean preferSms;
    private boolean preferWhatsApp;
    private boolean preferPhone;
}
