package com.himaloyit.buildnation.mm.domain.model;

import lombok.*;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommunicationPreferenceRequest {
    private boolean preferEmail;
    private boolean preferSms;
    private boolean preferWhatsApp;
    private boolean preferPhone;
}
