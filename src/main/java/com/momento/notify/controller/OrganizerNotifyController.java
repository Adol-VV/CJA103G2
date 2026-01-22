package com.momento.notify.controller;

import com.momento.emp.model.EmpVO;
import com.momento.notify.model.OrganizerNotifyService;
import com.momento.notify.model.OrganizerNotifyVO;
import com.momento.organizer.model.OrganizerVO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("getOrganizerNotifications")
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

//    @PostMapping("/addNotify")
//    public String addNotify(@ModelAttribute("organizerNotifyVO") OrganizerNotifyVO vo, RedirectAttributes radirect){
//        orgNotifySvc.addNotify(vo);
//        radirect.addFlashAttribute("message", "成功發送通知");
//        return "redirect:/organizer/notify/send-page";
//    }
    @PostMapping("/addNotify")
    public String addNotify(@ModelAttribute("organizerNotifyVO") OrganizerNotifyVO vo) {

        // 1. 解決 EMP_ID 不能為 null 的問題
        // 因為資料庫規定一定要有員工 ID，我們先暫時塞一個資料庫裡已經有的員工編號 (例如 1)
        EmpVO tempEmp = new EmpVO();
        tempEmp.setEmpId(1); // 請確認資料庫 EMP 表中是否有編號 1 的員工
        vo.setEmpVO(tempEmp);

        // 2. 同時也要塞入主辦方 ID (ORGANIZER_ID)
        // 否則接下來可能會換成報 ORGANIZER_ID cannot be null
        OrganizerVO tempOrg = new OrganizerVO();
        tempOrg.setOrganizerId(1); // 假設目前登入的主辦方編號是 1
        vo.setOrganizerVO(tempOrg);

        // 3. 執行儲存
        orgNotifySvc.addNotify(vo);

        return "redirect:/organizer/notify/send-page";
    }

    @GetMapping("/send-page")
    public String showSendPage(ModelMap model){
        List<OrganizerNotifyVO> list = orgNotifySvc.getByOrgId(1); // 假設目前主辦方 ID 為 1
        model.addAttribute("notifyListData", list);
        model.addAttribute("organizerNotifyVO", new OrganizerNotifyVO());
        model.addAttribute("targetPanel", "send-page");

//        model.addAttribute("notifyListData", orgNotifySvc.getAll());
        return "pages/organizer/dashboard";
    }

    @GetMapping("/getOrganizerNotify")
    public String getOrganizerNotify (
        @RequestParam String organizerId, ModelMap model){
        List<OrganizerNotifyVO> list = orgNotifySvc.getByOrgId(Integer.valueOf(organizerId));
        model.addAttribute("notifyListData", list);
        model.addAttribute("targetPanel", "notifications"); // 讓通知中心亮起
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
