package com.himaloyit.buildnation.ui.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Mirrors com.himaloyit.buildnation.mm.domain.model.CreateMemberRequest. Note {@code role} is a
 * plain String on the backend (parsed leniently server-side, falling back to GENERAL_MEMBER on a
 * bad value) even though every other role field in this service is the {@link MemberRole} enum —
 * this UI still only ever sends a valid enum name via a bounded ComboBox.
 */
@Data
@AllArgsConstructor
public class CreateMemberRequest {
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String address;
    private UUID constituencyId;
    private String role;
}
