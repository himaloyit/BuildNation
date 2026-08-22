package com.himaloyit.buildnation.ui.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.mm.domain.dto.MemberProfileDTO. */
@Data
@NoArgsConstructor
public class MemberProfileDTO {
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
}
