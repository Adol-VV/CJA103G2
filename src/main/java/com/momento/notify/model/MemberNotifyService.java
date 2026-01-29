package com.momento.notify.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberNotifyService {
    @Autowired
    private MemberNotifyRepository memberNotifyRepository;


    // 根據會員ID抓取該會員所有的通知紀錄 (依時間由新到舊排序)
    public List<MemberNotifyVO> getNotificationsByMemberId(Integer memberId) {
        return memberNotifyRepository.findByMemberVO_MemberIdOrderByCreatedAtDesc(memberId);
    }

    //更新通知為已讀狀態
    @Transactional
    public void markAsRead(Integer memberNotifyId) {
        memberNotifyRepository.findById(memberNotifyId).ifPresent(notify -> {
            notify.setIsRead(1); // 1 代表已讀
            memberNotifyRepository.save(notify);
        });
    }

    @Transactional
    public void  addMemberNotify(MemberNotifyVO memberNotifyVO){
        memberNotifyRepository.save(memberNotifyVO);
    }
}
