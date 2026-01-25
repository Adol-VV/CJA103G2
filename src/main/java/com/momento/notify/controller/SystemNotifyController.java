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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@Validated
@RequestMapping("/notify")
public class SystemNotifyController {
    @Autowired
    SystemNotifyService sysNotifySvc;

    @PostMapping("sendMessageNotify")
    public String sendMessageNotify(
            @NotEmpty(message = "通知類型: 請勿空白") @RequestParam("type") String type,
            @NotEmpty(message="通知標題: 請勿空白") @RequestParam("title") String title,
            @NotEmpty(message="通知內容: 請勿空白") @RequestParam("rawContent") String rawContent,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam("recipientGroup") String recipientGroup,
            @RequestParam("empId") Integer empId, // 暫時由參數接收
            ModelMap model, RedirectAttributes redirectAttributes) {

        try{
            sysNotifySvc.sendMessageNotify(type, title, rawContent, url, recipientGroup, empId);
            redirectAttributes.addFlashAttribute("successMessage", "通知已成功發送！");
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "發送失敗：" + e.getMessage());
        }

        List<Object[]> records = sysNotifySvc.getMessageNotifyRecords();
        model.addAttribute("massNotifyRecords", records);

        return "redirect:/admin/dashboard#notification-composer";
        }


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

            model.addAttribute("massNotifyRecords", sysNotifySvc.getMessageNotifyRecords());
            // 查詢完成,準備轉交
            return "pages/admin/dashboard";
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
                return "pages/admin/dashboard";
            }

            // 查詢完成,準備轉交
                model.addAttribute("sysNotifyVO", sysNotifyVO);
                return "pages/admin/dashboard";
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
        return new ModelAndView("pages/admin/dashboard", "errorMessage", "請修正以下錯誤:<br>"+message);
    }
}

