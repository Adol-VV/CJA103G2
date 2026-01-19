package com.momento.notify.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizerNotifyService {
    @Autowired
    private OrganizerNotifyRepository repository;

    public void addNotify(OrganizerNotifyVO organizerNotifyVO){
        repository.save(organizerNotifyVO);
    }

    public void updateNotify(OrganizerNotifyVO organizerNotifyVO){
        repository.save(organizerNotifyVO);
    }

    public void deleteNotify(Integer organizerNotifyId){
        if (repository.existsById(organizerNotifyId)) {
            repository.deleteById(organizerNotifyId);
        }
    }

    public OrganizerNotifyVO getOneNotify(Integer organizerNotifyId) {
        Optional<OrganizerNotifyVO> optional = repository.findById(organizerNotifyId);
        return optional.orElse(null); // 如果找不到就回傳 null
    }

    public List<OrganizerNotifyVO> getAll() {
        return repository.findAll();
    }
}
