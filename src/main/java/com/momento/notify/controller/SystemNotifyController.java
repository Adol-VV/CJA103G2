package com.momento.notify.controller;

import com.momento.notify.model.SystemNotifyService;
import com.momento.notify.model.SystemNotifyVO;
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
@RequestMapping("/notify")
public class SystemNotifyController {
    @Autowired
    SystemNotifyService sysNotifySvc;

    // 取得該會員的所有系統通知
    @PostMapping("getMemberNotifications")
    public String getMemberNotifications(
    // 接收請求參數, 輸入格式的錯誤處理
            @NotEmpty(message="會員編號: 請勿空白")
            @Digits(integer = 10, fraction = 0, message = "會員編號: 請填數字")
            @RequestParam("memberId") String memberId,  ModelMap model) {

            // 開始查詢資料
            List<SystemNotifyVO> list = sysNotifySvc.getByMemId(Integer.valueOf(memberId));
            model.addAttribute("notifyListData", list);

            if (list == null || list.isEmpty()) {
                model.addAttribute("errorMessage", "目前尚無通知訊息");
            }

            // 查詢完成,準備轉交
            return "pages/user/dashboard";
            }


     // 單一通知詳情，並觸發「已讀」邏輯
    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            @NotEmpty(message="通知編號: 請勿空白")
            @RequestParam("systemNotifyId") String systemNotifyId,
            ModelMap model) {

            // 開始查詢與更新狀態
            SystemNotifyVO sysNotifyVO = sysNotifySvc.getOneSystemNotify(Integer.valueOf(systemNotifyId));

            // 邏輯：如果狀態為 0 (未讀)，則更新為 1 (已讀)
            if (sysNotifyVO != null && sysNotifyVO.getNotifyStatus() == 0) {
                sysNotifySvc.updateReadStatus(Integer.valueOf(systemNotifyId), 1);
            }

            if (sysNotifyVO == null) {
                model.addAttribute("errorMessage", "查無此通知內容");
                return "pages/user/dashboard";
            }

            // 查詢完成,準備轉交
                model.addAttribute("sysNotifyVO", sysNotifyVO);
                return "pages/user/dashboard";
            }

     // 錯誤處理
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ModelAndView handleError(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations ) {
            strBuilder.append(violation.getMessage()).append("<br>");
        }
        String message = strBuilder.toString();
        return new ModelAndView("pages/user/dashboard", "errorMessage", "請修正以下錯誤:<br>"+message);
    }
}

