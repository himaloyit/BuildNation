package com.himaloyit.buildnation.ui.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.mm.domain.model.UpdateMemberProfileRequest. */
@Data
@AllArgsConstructor
public class UpdateMemberProfileRequest {
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
