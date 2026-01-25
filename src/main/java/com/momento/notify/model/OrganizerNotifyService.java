package com.momento.notify.model;

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

    @Transactional
    public void addNotify(OrganizerNotifyVO organizerNotifyVO){
        repository.save(organizerNotifyVO);
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
}
