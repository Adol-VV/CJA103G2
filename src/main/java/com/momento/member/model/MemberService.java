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
		Optional<MemberVO> member = repository.findById(memberId);
		return member.orElseThrow(() -> new RuntimeException("找不到該會員"));
	}
	
	public List<MemberVO> findByNameLike(String name){
		return repository.findByName(name);
	}
	
	public MemberVO findByAccount(String account) {
		Optional<MemberVO> optional = Optional.ofNullable(repository.findByAccount(account));
		return optional.orElse(null);
	}
	
	public List<MemberVO> searchMembers(Integer status, String keyword) {
        // 處理關鍵字：如果是空字串或全是空格，轉為 null
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        
        // 處理狀態：假設前端「所有狀態」傳來的是 -1 或 null
        Integer searchStatus = (status != null && status != -1) ? status : null;

        return repository.findByFilters(searchStatus, searchKeyword);
    }
}
