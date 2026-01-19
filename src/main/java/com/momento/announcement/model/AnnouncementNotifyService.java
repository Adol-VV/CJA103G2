package com.momento.announcement.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementNotifyService {
    @Autowired
    private AnnouncementNotifyRepository repository;

    public void addAnnouncement(AnnouncementVO announcementVO){
        repository.save(announcementVO);
    }

    public void updateAnnouncement(AnnouncementVO announcementVO){
        repository.save(announcementVO);
    }

    public void deleteAnnouncement(Integer announcementId){
        repository.deleteById(announcementId);
    }

    public AnnouncementVO getOneAnnouncement(Integer announcementId){
        return repository.findById(announcementId).orElse(null);
    }

    public List<AnnouncementVO> getAll(){
        return repository.findAll();
    }
}
