package com.momento.notify.model;

import com.momento.member.model.MemberRepository;
import com.momento.member.model.MemberVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizerNotifyService {
    @Autowired
    private OrganizerNotifyRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MemberNotifyRepository memNotifyRepo;
    @Autowired
    private MemberRepository memberRepository;


    @Transactional
    public void addNotify(OrganizerNotifyVO organizerNotifyVO){
        repository.save(organizerNotifyVO);
        List<MemberVO> allMembers = memberRepository.findAll(); // 從MemberRepository動態獲取所有會員

        for (MemberVO member : allMembers){
            MemberNotifyVO memberNotify = new MemberNotifyVO();
            memberNotify.setMemberVO(member);
            memberNotify.setOrganizerNotifyVO(organizerNotifyVO);
            memberNotify.setTitle(organizerNotifyVO.getTitle());
            memberNotify.setContent(organizerNotifyVO.getContent());
            memberNotify.setIsRead(0);
            memberNotify.setCreatedAt(java.time.LocalDateTime.now());

            memNotifyRepo.save(memberNotify);
        }
    }
    public List<OrganizerNotifyVO> getNotifiesByOrganizer(Integer organizerId) {
        return repository.findByOrganizerVO_OrganizerIdOrderByCreatedAtDesc(organizerId);
    }

    @Transactional
    public void updateNotify(OrganizerNotifyVO organizerNotifyVO){
        repository.save(organizerNotifyVO);
    }

    @Transactional
    public void deleteNotify(Integer organizerNotifyId){
        if (repository.existsById(organizerNotifyId)) {
            repository.deleteById(organizerNotifyId);
        }
    }

    public OrganizerNotifyVO getOneOrganizerNotify(Integer organizerNotifyId) {
        Optional<OrganizerNotifyVO> optional = repository.findById(organizerNotifyId);
        return optional.orElse(null); // 如果找不到就回傳 null
    }



    public List<OrganizerNotifyVO> getByOrgId(Integer organizerId){
        return repository.findByOrganizerVO_OrganizerIdOrderByCreatedAtDesc(organizerId);
    }

    public List<OrganizerNotifyVO> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void updateReadStatus(Integer systemNotifyId, Integer status){
        Optional<OrganizerNotifyVO> optional = repository.findById(systemNotifyId);
            if (optional.isPresent()){
                OrganizerNotifyVO vo = optional.get();
                vo.setIsRead(status);
                repository.save(vo);
            }
        }

    /**
     * 標記該主辦方所有通知為已讀
     */
    @Transactional
    public void markAllAsReadByOrgId(Integer organizerId) {
        List<OrganizerNotifyVO> notifications = repository.findByOrganizerVO_OrganizerIdOrderByCreatedAtDesc(organizerId);
        for (OrganizerNotifyVO notify : notifications) {
            if (notify.getIsRead() != null && notify.getIsRead() == 0) {
                notify.setIsRead(1);
                repository.save(notify);
            }
        }
    }

    /**
     * 刪除該主辦方所有通知
     */
    @Transactional
    public void deleteAllByOrgId(Integer organizerId) {
        List<OrganizerNotifyVO> notifications = repository.findByOrganizerVO_OrganizerIdOrderByCreatedAtDesc(organizerId);
        repository.deleteAll(notifications);
    }
}
