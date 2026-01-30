package com.momento.announcement.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementNotifyService {
    @Autowired
    private AnnouncementNotifyRepository repository;

    public AnnouncementVO addAnnouncement(AnnouncementVO announcementVO){
        return repository.save(announcementVO);
    }

    public AnnouncementVO updateAnnouncement(AnnouncementVO announcementVO){
        return repository.save(announcementVO);
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

    // 取得所有公告
    public List<AnnouncementVO> getAllByCreatedAtDesc(){
        return repository.findAllByOrderByCreatedAtDesc();
    }

    // 取得公告總數
    public long getCount(){
        return repository.count();
    }
}
