package com.himaloyit.buildnation.ui.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.mm.domain.dto.CommunicationPreferenceDTO. */
@Data
@NoArgsConstructor
public class CommunicationPreferenceDTO {
    private UUID id;
    private boolean preferEmail;
    private boolean preferSms;
    private boolean preferWhatsApp;
    private boolean preferPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
