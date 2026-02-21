package com.himaloyit.buildnation.mm.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "communication_preferences")
public class CommunicationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Builder.Default
    private boolean preferEmail = false;

    @Builder.Default
    private boolean preferSms = false;

    @Builder.Default
    private boolean preferWhatsApp = false;

    @Builder.Default
    private boolean preferPhone = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "communicationPreference")
    @JsonBackReference("member-communication")
    private Member member;
}
