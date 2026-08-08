package com.himaloyit.buildnation.cdm.domain.entities;

import com.himaloyit.buildnation.cdm.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.domain.enums.ContractorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cons_contractor")
public class Contractor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractorType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractorStatus status;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String address;

    private String license;

    @Column(nullable = false)
    private String keyPersonName;

    @Column(nullable = false)
    private String keyPersonContact;

    private String bankName;
    private String bankAccountNumber;
    private String bankBranch;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
