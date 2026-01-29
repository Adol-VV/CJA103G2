package com.momento.notify.model;

import com.momento.eventorder.model.EventOrderVO;
import com.momento.eventreview.dto.EventReviewDTO;
import com.momento.prodorder.model.ProdOrderIdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationBridgeService {
    @Autowired
    private MemberNotifyService memberNotifyService;
    @Autowired
    private OrganizerNotifyService organizerNotifyService;
    @Autowired
    private SystemNotifyService systemNotifyService;
    @Autowired
    private OrganizerNotifyRepository organizerNotifyRepository;

    // 當活動下單時, 觸發這邊的方法 (EventOrderService)
    @Transactional
    public void processEventOrderNotify(EventOrderVO orderVO){
        String timeStr = orderVO.getCreatedAt().toString();

        // 通知會員
        MemberNotifyVO memberNotifyVO = new MemberNotifyVO();
        memberNotifyVO.setMemberVO(orderVO.getMember());
        memberNotifyVO.setTitle("活動訂單成立通知");
        memberNotifyVO.setContent("您購買的活動『" + orderVO.getEvent().getEventId() + "』已於 " + timeStr + " 下單成功。");
        memberNotifyVO.setIsRead(0);
        memberNotifyService.addMemberNotify(memberNotifyVO);

        // 通知主辦方
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();
        organizerNotifyVO.setOrganizerVO(orderVO.getOrganizer());
        organizerNotifyVO.setTitle("您有新的活動訂單");
        organizerNotifyVO.setContent("訂單編號 #" + orderVO.getEventOrderId() + "已成立，請至後台查看。");
        organizerNotifyVO.setIsRead(0);
        organizerNotifyRepository.save(organizerNotifyVO);
    }

    // 當商品下單時, 觸發這邊 (ProdOrderIdService)
    @Transactional
    public void processProdOrderNotify(ProdOrderIdVO prodOrderIdVO) {

        // 通知會員
        MemberNotifyVO memberNotifyVO = new MemberNotifyVO();
        memberNotifyVO.setMemberVO(prodOrderIdVO.getMemberId());
        memberNotifyVO.setTitle("商品訂單付款成功");
        memberNotifyVO.setContent("您的商品訂單已於 " + prodOrderIdVO.getCreatedDate() + " 成立，金額為 $" + prodOrderIdVO.getPayable());
        memberNotifyVO.setIsRead(0);
        memberNotifyService.addMemberNotify(memberNotifyVO);

        // 通知主辦方
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();
        organizerNotifyVO.setOrganizerVO(prodOrderIdVO.getOrganizerId());
        organizerNotifyVO.setTitle("新商品訂單提醒");
        organizerNotifyVO.setContent("主辦方您好，您有一筆新的商品訂單，金額：$" + prodOrderIdVO.getPayable());
        organizerNotifyVO.setIsRead(0);
        organizerNotifyRepository.save(organizerNotifyVO);
    }

    // 活動審核通知 (EventReviewService)
    @Transactional
    public void processEventReviewNotify(EventReviewDTO eventReviewDTO){
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();
        organizerNotifyVO.setTitle(eventReviewDTO.getStatus() == 1 ? "活動審核通過" : "活動審核駁回");
        organizerNotifyVO.setContent("活動『" + eventReviewDTO.getTitle() + "』審核結果 : " + (eventReviewDTO.getStatus() == 1 ? "已上架" : "駁回，原因 : " + eventReviewDTO.getRejectReason()));
        organizerNotifyVO.setIsRead(0);
        organizerNotifyRepository.save(organizerNotifyVO);
    }

    // 商品審核通知 (EmpController)

}
