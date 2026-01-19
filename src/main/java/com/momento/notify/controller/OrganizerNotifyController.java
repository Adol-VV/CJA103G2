package com.momento.notify.controller;

import com.momento.notify.model.OrganizerNotifyService;
import com.momento.notify.model.OrganizerNotifyVO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;

@Controller
@Validated
@RequestMapping("/organizer/notify")
public class OrganizerNotifyController {
    @Autowired
    OrganizerNotifyService orgNotifySvc;

    //取得主辦方的所有通知
    @PostMapping("getOrganizerNotifications")
    public String getOrganizerNotifications(
    // 接收請求參數, 輸入格式的錯誤處理
            @NotEmpty(message = "主辦方編號: 請勿空白")
            @Digits(integer = 10, fraction = 0, message = "主辦方編號: 請填數字")
            @RequestParam("organizerId") String organizerId, ModelMap model){
        // 開始查詢資料
        List<OrganizerNotifyVO> list = orgNotifySvc.getByOrgId(Integer.valueOf(organizerId));
        model.addAttribute("notifyListData", list);

        if(list == null || list.isEmpty()){
            model.addAttribute("errorMessage", "目前尚無通知訊息");
        }

        // 查詢完成, 準備轉交

        return "pages/organizer/dashboard";
    }

    // 單一通知詳情
    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            @NotEmpty(message = "通知編號: 請勿空白")
            @RequestParam("organizerNotifyId") String organizerNotifyId, ModelMap model){

                    // 查詢與更新狀態
                    OrganizerNotifyVO orgNotifyVO = orgNotifySvc.getOneOrganizerNotify(Integer.valueOf(organizerNotifyId));

                    // 狀態為0(未讀), 更新為1(已讀)
                    if (orgNotifyVO != null && orgNotifyVO.getNotifyStatus() == 0){
                        orgNotifySvc.updateReadStatus(Integer.valueOf(organizerNotifyId), 1);
                    }

                    if (orgNotifyVO == null){
                        model.addAttribute("errorMessage", "查無此通知內容");
                        return "pages/organizer/dashboard";
                    }

                    // 查詢完成, 準備轉交
                    model.addAttribute("orgNotifyVO", orgNotifyVO);
                    return "pages/organizer/dashboard";
            }

            // 錯誤處理
            @ExceptionHandler(value = {ConstraintViolationException.class})
            public ModelAndView handleError(ConstraintViolationException e){
                Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
                StringBuilder strBuilder = new StringBuilder();
                for (ConstraintViolation<?> violation: violations){
                    strBuilder.append(violation.getMessage()).append("<br>");
                }
                String message = strBuilder.toString();
                return new ModelAndView("pages/organizer/dashboard", "errorMessage", "請修正以下錯誤:<br>"+message);
            }


}
