package com.himaloyit.buildnation.mm.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationPreferenceDTO {
    private UUID id;
    private boolean preferEmail;
    private boolean preferSms;
    private boolean preferWhatsApp;
    private boolean preferPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
