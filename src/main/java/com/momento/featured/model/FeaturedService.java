package com.momento.featured.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.momento.event.model.EventImageRepository;
import com.momento.event.model.EventImageVO;
import com.momento.event.model.EventVO;

@Service
public class FeaturedService {

    @Autowired
    private FeaturedRepository repository;

    // 注入 EventImageRepository 以便查詢封面圖
    @Autowired
    private EventImageRepository eventImageRepository;

    // 保留原本的 CRUD 方法...
    public FeaturedVO addFeatured(FeaturedVO featuredVO) {
        return repository.save(featuredVO);
    }

    public FeaturedVO updateFeatured(FeaturedVO featuredVO) {
        return repository.save(featuredVO);
    }

    public void deleteFeatured(Integer featuredId) {
        if (repository.existsById(featuredId)) {
            repository.deleteById(featuredId);
        }
    }

    public FeaturedVO getOneFeatured(Integer featuredId) {
        Optional<FeaturedVO> optional = repository.findById(featuredId);
        return optional.orElse(null);
    }

    public List<FeaturedVO> getAll() {
        return repository.findAll();
    }

    /**
     * 取得首頁輪播專用的資料 (組裝 Featured + Event + Image)
     */
    @Transactional(readOnly = true)
    public List<FeaturedCarouselDTO> getCarouselData() {
        List<FeaturedVO> featuredList = repository.findAll();
        List<FeaturedCarouselDTO> resultList = new ArrayList<>();

        for (FeaturedVO featured : featuredList) {
            EventVO event = featured.getEventVO();
            // 防呆：如果關聯的活動不存在，則跳過
            if (event == null) continue;

            // 查詢該活動的第一張圖片 (封面圖)
            Optional<EventImageVO> imgOpt = eventImageRepository.findFirstByEvent_EventIdOrderByEventImageIdAsc(event.getEventId());
            
            // 如果有圖就用資料庫的圖，沒圖就用預設圖 (可自行換成專案內的預設圖路徑)
            String imgUrl = imgOpt.map(EventImageVO::getImageUrl)
                                  .orElse("https://via.placeholder.com/1920x600/2D5F4F/FFFFFF?text=No+Image");

            // 組裝 DTO
            FeaturedCarouselDTO dto = new FeaturedCarouselDTO();
            dto.setFeaturedId(featured.getFeaturedId());
            dto.setEventId(event.getEventId());
            dto.setTitle(event.getTitle());      // 對應 EventVO 的 title
            dto.setContent(event.getContent());  // 對應 EventVO 的 content
            dto.setImageUrl(imgUrl);
            
            resultList.add(dto);
        }
        return resultList;
    }

    // 內部類別 DTO：專門給 Controller -> View 傳輸資料用
    public static class FeaturedCarouselDTO {
        private Integer featuredId;
        private Integer eventId;
        private String title;
        private String content;
        private String imageUrl;

        // Getters and Setters
        public Integer getFeaturedId() { return featuredId; }
        public void setFeaturedId(Integer featuredId) { this.featuredId = featuredId; }
        public Integer getEventId() { return eventId; }
        public void setEventId(Integer eventId) { this.eventId = eventId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}