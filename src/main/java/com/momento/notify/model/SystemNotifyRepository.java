package com.momento.notify.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemNotifyRepository extends JpaRepository<SystemNotifyVO, Integer> {
    List<SystemNotifyVO> findByMemberVO_MemberIdOrderByCreatedAtDesc(Integer memberId);
}
