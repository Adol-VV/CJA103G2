package com.momento.notify.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberNotifyRepository extends JpaRepository<MemberNotifyVO, Integer> {
    List<MemberNotifyVO> findByMemberVO_MemberIdOrderByCreatedAtDesc(Integer memberId);
}
