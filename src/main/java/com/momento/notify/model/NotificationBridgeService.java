package com.momento.notify.model;

import com.momento.emp.model.EmpService;
import com.momento.emp.model.EmpVO;
import com.momento.event.model.EventVO;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.organizer.model.OrganizerVO;
import com.momento.prod.dto.ProdDTO;
import com.momento.prod.model.ProdService;
import com.momento.prodorder.model.ProdOrderIdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class NotificationBridgeService {
    @Autowired
    private MemberNotifyService memberNotifyService;
    @Autowired
    private OrganizerNotifyService organizerNotifyService;
    @Autowired
    private SystemNotifyService systemNotifyService;
    @Autowired
    private MemberNotifyRepository memberNotifyRepository;
    @Autowired
    private OrganizerNotifyRepository organizerNotifyRepository;
    @Autowired
    private EmpService empService;
    @Autowired
    public ProdService prodService;

    // 抓系統管理員
    private EmpVO getSystemAdmin(){
        EmpVO admin = empService.getEmployeeByAccount("admin");
        if (admin == null){
            List<EmpVO> allEmp = empService.getAllEmployees();
            return allEmp.isEmpty() ? null : allEmp.get(0);
        }
        return admin;
    }

    // 當活動下單時, 觸發這邊的方法 (CreateOrderService)、//(EventOrderService)
    @Transactional
    public void processEventOrderNotify(EventOrderVO orderVO){
        // 格式化時間
        String timeStr = "今日";
        if (orderVO.getCreatedAt() != null) {
            timeStr = orderVO.getCreatedAt().toString().replace("T", " ").substring(0, 16);
        }
        // 取得活動名稱
        String eventTitle = orderVO.getEvent() != null ? orderVO.getEvent().getTitle() : "活動";
        // 取得票數
        int ticketCount = (orderVO.getEventOrderItems() != null) ? orderVO.getEventOrderItems().size() : 0;
        // 取得金額
        int payable = orderVO.getPayable() != null ? orderVO.getPayable() : 0;

        // 通知會員
        MemberNotifyVO memberNotifyVO = new MemberNotifyVO();
        memberNotifyVO.setMemberVO(orderVO.getMember());
        memberNotifyVO.setTitle("活動訂單成立通知");
        memberNotifyVO.setContent("您購買的活動『" + eventTitle + "』已於 " + timeStr + " 下單成功，共 " + ticketCount + " 張票券，請至會員中心查看電子票券。");
        memberNotifyVO.setIsRead(0);
        memberNotifyService.addMemberNotify(memberNotifyVO);

        // 通知主辦方
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();
        organizerNotifyVO.setOrganizerVO(orderVO.getOrganizer());
        organizerNotifyVO.setTitle("新活動訂單 #" + orderVO.getEventOrderId());
        organizerNotifyVO.setContent("您的活動『" + eventTitle + "』收到新訂單，票數：" + ticketCount + " 張，金額：$" + payable + "，請至後台查看訂單詳情。");
        organizerNotifyVO.setIsRead(0);
        organizerNotifyRepository.save(organizerNotifyVO);
    }

    // 當商品下單時, 觸發這邊 (ProdOrderIdService)
    @Transactional
    public void processProdOrderNotify(ProdOrderIdVO prodOrderIdVO) {
        // 格式化日期
        String dateStr = "今日";
        if (prodOrderIdVO.getCreatedDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            dateStr = sdf.format(prodOrderIdVO.getCreatedDate());
        }
        // 取得金額，預設為 0
        int payable = prodOrderIdVO.getPayable() != null ? prodOrderIdVO.getPayable() : 0;
        // 取得訂單編號
        Integer orderId = prodOrderIdVO.getOrderId();

        // 取得商品名稱列表 
        StringBuilder prodNames = new StringBuilder();
        if (prodOrderIdVO.getOrderItems() != null && !prodOrderIdVO.getOrderItems().isEmpty()) {
            for (int i = 0; i < prodOrderIdVO.getOrderItems().size(); i++) {
                var item = prodOrderIdVO.getOrderItems().get(i);
                if (item.getProdId() != null) {
                    // 取得商品名稱
                    Integer prodId = item.getProdId().getProdId();
                    if (prodId != null) {
                        var prodDTO = prodService.getOneProd(prodId);
                        if (prodDTO != null && prodDTO.getProdName() != null) {
                            if (i > 0) prodNames.append("、");
                            prodNames.append(prodDTO.getProdName());
                        }
                    }
                }
            }
        }
        String prodNameStr = !prodNames.isEmpty() ? prodNames.toString() : "商品";

        // 通知會員
        MemberNotifyVO memberNotifyVO = new MemberNotifyVO();
        memberNotifyVO.setMemberVO(prodOrderIdVO.getMemberId());
        memberNotifyVO.setTitle("商品訂單付款成功");
        memberNotifyVO.setContent("您購買的商品『" + prodNameStr + "』已於 " + dateStr + " 下單成功，金額：$" + payable + "，賣家將盡快為您安排出貨。");
        memberNotifyVO.setIsRead(0);
        memberNotifyService.addMemberNotify(memberNotifyVO);

        // 通知主辦方
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();
        organizerNotifyVO.setOrganizerVO(prodOrderIdVO.getOrganizerId());
        organizerNotifyVO.setTitle("新商品訂單 #" + orderId);
        organizerNotifyVO.setContent("您有一筆新訂單，商品：" + prodNameStr + "，金額：$" + payable + "，請盡快安排出貨。");
        organizerNotifyVO.setIsRead(0);
        organizerNotifyRepository.save(organizerNotifyVO);
    }

    // 活動審核通知 (EventReviewService)
    @Transactional
    public void processEventReviewNotify(EventVO event, boolean isPassed, String reason){
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();
        organizerNotifyVO.setEmpVO(getSystemAdmin()); // 發送者:系統管理員
        organizerNotifyVO.setOrganizerVO(event.getOrganizer()); // 接收者:對應到的主辦方
        if (isPassed){
            organizerNotifyVO.setTitle("活動審核通過");
            organizerNotifyVO.setContent("恭喜！您的活動『" + event.getTitle() + "』已審核通過。");
        } else {
            organizerNotifyVO.setTitle("活動審核駁回通知");
            organizerNotifyVO.setContent("很抱歉，您的活動『" + event.getTitle() + "』未通過審核。原因：" + reason);
        }

        organizerNotifyVO.setIsRead(0);
        organizerNotifyVO.setTargetId(String.valueOf(event.getEventId())); // 連結到活動ID
        organizerNotifyRepository.save(organizerNotifyVO);
    }

    // 商品審核通知 (EmpController)
    @Transactional
    public void processProdReviewNotify(Integer prodId, Byte reviewStatus){
        ProdDTO prodDTO = prodService.getOneProd(prodId);
        if (prodDTO == null)
            return;

        // 建立通知物件
        OrganizerNotifyVO organizerNotifyVO = new OrganizerNotifyVO();

        // 設定發送者: 管理員
        organizerNotifyVO.setEmpVO(getSystemAdmin());

        // 設定接收者: 主辦方物件並設定ID
        OrganizerVO organizerVO = new OrganizerVO();
        organizerVO.setOrganizerId(prodDTO.getOrganizerId());
        organizerNotifyVO.setOrganizerVO(organizerVO);

        // 判斷(1為通過、2為未通過)
        if (reviewStatus == 1){
            organizerNotifyVO.setTitle("商品審核通過");
            organizerNotifyVO.setContent("恭喜！您的商品『" + prodDTO.getProdName() + "』已審核通過");
        } else {
            organizerNotifyVO.setTitle("商品審核駁回通知");
            organizerNotifyVO.setContent("您的商品『" + prodDTO.getProdName() + "』未通過審核，請確認規格與內容後重新提交。");
        }

        organizerNotifyVO.setIsRead(0);
        organizerNotifyVO.setTargetId(String.valueOf(prodId));

        organizerNotifyRepository.save(organizerNotifyVO);
    }

}
