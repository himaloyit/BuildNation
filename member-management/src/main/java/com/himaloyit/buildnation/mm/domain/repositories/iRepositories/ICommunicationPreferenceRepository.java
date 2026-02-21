package com.himaloyit.buildnation.mm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.mm.domain.entities.CommunicationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface ICommunicationPreferenceRepository extends JpaRepository<CommunicationPreference, UUID> {

    Optional<CommunicationPreference> findByMemberId(UUID memberId);
}
