package com.himaloyit.buildnation.mm.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_profiles")
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String dob;
    private String gender;
    private String nationality;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String instagramUrl;
    private String websiteUrl;

    @OneToOne(mappedBy = "profile")
    @JsonBackReference("member-profile")
    private Member member;
}
