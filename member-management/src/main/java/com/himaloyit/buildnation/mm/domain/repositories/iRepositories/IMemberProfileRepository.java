package com.himaloyit.buildnation.mm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.mm.domain.entities.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IMemberProfileRepository extends JpaRepository<MemberProfile, UUID> {

    Optional<MemberProfile> findByMemberId(UUID memberId);
}
