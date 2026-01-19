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

    public void deleteNotify(Integer systemNotifiId){
        repository.existsById(systemNotifiId);
    }

    public SystemNotifyVO getOneSystemNotify(Integer systemNotifyId) {
        return repository.findById(systemNotifyId).orElse(null);
    }

    public List<SystemNotifyVO> getAll() {
        return repository.findAll();
    }
}
