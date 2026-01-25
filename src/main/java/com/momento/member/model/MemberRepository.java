package com.momento.member.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MemberRepository extends JpaRepository<MemberVO, Integer>{
	
	@Transactional
	@Modifying
	@Query(value = "delete from member where member_id =?1", nativeQuery = true)
	public void deleteByMemberId(int memberId);
	
	public MemberVO findByAccount(String account);
	
	public List<MemberVO> findByName(String name);
	
	@Modifying
	@Query("UPDATE MemberVO m SET m.token = :newToken WHERE m.memberId = :id")
	void updateToken(Integer newToken,Integer id);
}
