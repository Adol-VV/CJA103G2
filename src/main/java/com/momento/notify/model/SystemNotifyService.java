package com.momento.notify.model;

import com.momento.emp.model.EmpVO;
import com.momento.member.model.MemberRepository;
import com.momento.member.model.MemberVO;
import com.momento.organizer.model.OrganizerRepository;
import com.momento.organizer.model.OrganizerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemNotifyService {
    @Autowired
    private SystemNotifyRepository repository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Transactional
    public void sendMessageNotify(String type, String title, String rawContent, String url, String recipientGroup, Integer empId) {
        String packagedContent = String.format("[%s]|%s|%s", type, (url == null ? "" : url), rawContent);

        EmpVO emp = new EmpVO();
        emp.setEmpId(empId);

        List<SystemNotifyVO> finalNotifyList = new ArrayList<>();

        // 一般會員
        if ("all".equals(recipientGroup) || recipientGroup.startsWith("front_")) {
            List<MemberVO> members = memberRepository.findAll();
            List<MemberVO> targetMembers = filterMembers(members, recipientGroup);

            for (MemberVO mem : targetMembers) {
                SystemNotifyVO vo = createBaseVO(title, packagedContent, emp);
                vo.setMemberVO(mem); // 存入 MEMBER_ID
                finalNotifyList.add(vo);
            }
        }

        // 主辦方
        if ("all".equals(recipientGroup) || recipientGroup.startsWith("org_")) {
            List<OrganizerVO> organizers = organizerRepository.findAll();
            List<OrganizerVO> targetOrganizers = filterOrganizers(organizers, recipientGroup);

            for (OrganizerVO org : targetOrganizers) {
                SystemNotifyVO vo = createBaseVO(title, packagedContent, emp);
                vo.setOrganizerVO(org); // 存入 ORGANIZER_ID
                finalNotifyList.add(vo);
            }
        }
        if (!finalNotifyList.isEmpty()) {
            repository.saveAll(finalNotifyList);
        }
    }

    private SystemNotifyVO createBaseVO(String title, String content, EmpVO emp) {
        SystemNotifyVO vo = new SystemNotifyVO();
        vo.setTitle(title);
        vo.setContent(content);
        vo.setEmpVO(emp);
        vo.setIsRead(0); // 預設未讀
        return vo;
    }

    // 過濾一般會員
    private List<MemberVO> filterMembers(List<MemberVO> list, String group) {
        if ("all".equals(group) || "front_all".equals(group)) return list;
        if ("front_active".equals(group)) {
            // 0:正常
            return list.stream().filter(m -> m.getStatus() != null && m.getStatus() == 0).toList();
        }
        if ("front_new".equals(group)) {
            // 新會員(30天內)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            return list.stream().filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(thirtyDaysAgo)).toList();
        }
        return new ArrayList<>();
    }

    // 過濾主辦方
    private List<OrganizerVO> filterOrganizers(List<OrganizerVO> list, String group) {
        if ("all".equals(group) || "org_all".equals(group)) return list;
        if ("org_active".equals(group)) {
            // 1:正常
            return list.stream().filter(o -> o.getStatus() != null && o.getStatus() == 1).toList();
        }
        return new ArrayList<>();
    }


    public void addNotify(SystemNotifyVO systemNotifyVO){
        repository.save(systemNotifyVO);
    }

    public void updateNotify(SystemNotifyVO systemNotifyVO){
        repository.save(systemNotifyVO);
    }

    @Transactional
    public void deleteNotify(Integer systemNotifyId){
        if (repository.existsById(systemNotifyId)) {
            repository.deleteById(systemNotifyId);
        }
    }

    public SystemNotifyVO getOneSystemNotify(Integer systemNotifyId) {
        return repository.findById(systemNotifyId).orElse(null);
    }

    @Transactional
    public void updateReadStatus(Integer sysNotifyId, Integer status){
        repository.findById(sysNotifyId).ifPresent(vo -> {
            vo.setIsRead(status);
            repository.save(vo);
        });
    }

    public List<SystemNotifyVO> getByMemId(Integer memberId){
        return repository.findByMemberVO_MemberIdOrderByCreatedAtDesc(memberId);
    }

    public List<SystemNotifyVO> getAll() {

        return repository.findAll();
    }

    // 取得群發通知的統計紀錄
    public List<Object[]> getMessageNotifyRecords() {
        return repository.findGroupedNotifyRecords();
    }
}
