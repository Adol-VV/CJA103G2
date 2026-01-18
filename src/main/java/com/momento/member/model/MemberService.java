package com.momento.member.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("memberService")
public class MemberService {
	
	@Autowired
	MemberRepository repository;
	
	public void addMember(MemberVO memberVO){
		repository.save(memberVO);
	}
	
	public void updateMember(MemberVO memberVO) {
		repository.save(memberVO);
	}
	
	public void deleteMember(Integer memberId) {
		if(repository.existsById(memberId)) {
			repository.deleteByMemberId(memberId);
		}
	}
	
	public List<MemberVO> getAll(){
		return repository.findAll();
	}
	
	public MemberVO findOneMember(Integer memberId) {
		Optional<MemberVO> optional = repository.findById(memberId);
		return optional.orElse(null);
	}
	
	public List<MemberVO> findByNameLike(String name){
		return repository.findByName(name);
	}
	
	public MemberVO findByAccount(String account) {
		Optional<MemberVO> optional = Optional.ofNullable(repository.findByAccount(account));
		return optional.orElse(null);
	}
}
