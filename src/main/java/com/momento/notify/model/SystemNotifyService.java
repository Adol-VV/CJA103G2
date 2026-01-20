package com.momento.notify.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemNotifyService {
    @Autowired
    private SystemNotifyRepository repository;

    public void addNotify(SystemNotifyVO systemNotifyVO){
        repository.save(systemNotifyVO);
    }

    public void updateNotify(SystemNotifyVO systemNotifyVO){
        repository.save(systemNotifyVO);
    }

    public void deleteNotify(Integer systemNotify){
        repository.existsById(systemNotify);
    }

    public SystemNotifyVO getOneSystemNotify(Integer systemNotifyId) {
        return repository.findById(systemNotifyId).orElse(null);
    }

    public void updateReadStatus(Integer sysNotifyId, Integer status){

    }

    public List<SystemNotifyVO> getByMemId(Integer memberId){
        return repository.findByMemberVO_MemberIdOrderByCreatedAtDesc(memberId);
    }

    public List<SystemNotifyVO> getAll() {

        return repository.findAll();
    }
}
