package com.himaloyit.buildnation.mm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.mm.domain.entities.Member;
import com.himaloyit.buildnation.mm.domain.enums.MemberRole;
import com.himaloyit.buildnation.mm.domain.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IMemberRepository extends JpaRepository<Member, UUID> {

    Page<Member> findByRole(MemberRole role, Pageable pageable);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);
}
