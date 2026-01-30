package com.momento.organizer.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerRepository extends JpaRepository<OrganizerVO, Integer> {

    Optional<OrganizerVO> findByAccount(String account);

    Optional<OrganizerVO> findByEmail(String email);

    boolean existsByAccount(String account);

    boolean existsByEmail(String email);

    List<OrganizerVO> findByStatus(Byte status);

    List<OrganizerVO> findByStatusOrderByCreatedAtAsc(Byte status);

    List<OrganizerVO> findByNameContainingIgnoreCaseAndStatus(String name, Byte status);
}
