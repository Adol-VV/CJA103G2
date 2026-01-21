package com.momento.notify.controller;

import com.momento.emp.model.EmpVO;
import com.momento.notify.model.OrganizerNotifyService;
import com.momento.notify.model.OrganizerNotifyVO;
import com.momento.organizer.model.OrganizerVO;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
    public String addNotify(@ModelAttribute("organizerNotifyVO") OrganizerNotifyVO vo, RedirectAttributes redirect) {
        if (vo.getEmpVO() == null){
            // 塞資料庫裡已有的員工編號 (ex  1)
            EmpVO tempEmp = new EmpVO();
            tempEmp.setEmpId(1);
            vo.setEmpVO(tempEmp);
        }

        if (vo.getOrganizerVO() == null){
            // 塞入主辦方 ID (ORGANIZER_ID)
            OrganizerVO tempOrg = new OrganizerVO();
            tempOrg.setOrganizerId(1);
            vo.setOrganizerVO(tempOrg);
        }

        //  執行儲存
        orgNotifySvc.addNotify(vo);

        // 發送成功訊息提示
        redirect.addFlashAttribute("successMessage", "成功發送通知");

        return "redirect:/organizer/notify/send-page";
    }

    @GetMapping("/send-page")
    public String showSendPage(ModelMap model, HttpSession session){
        // 串接登入後, 改為Integer organizerId = (Integer) session.getAttribute("organizerId");
        Integer organizerId = 1; // 假設目前主辦方 ID 為 1
        OrganizerVO organizer = new OrganizerVO();
        organizer.setOrganizerId(organizerId);
        organizer.setName("測試主辦方(ID:1)");

        model.addAttribute("targetPanel", "send-page");
        model.addAttribute("organizer", organizer);
        model.addAttribute("organizerNotifyVO", new OrganizerNotifyVO());

        try {

            List<OrganizerNotifyVO> list = orgNotifySvc.getByOrgId(organizerId);
            model.addAttribute("notifyListData", list);
        } catch (Exception e) {
            model.addAttribute("notifyListData", new ArrayList<OrganizerNotifyVO>());
        }

//        model.addAttribute("notifyListData", list);
//        model.addAttribute("organizerNotifyVO", new OrganizerNotifyVO());
//        model.addAttribute("targetPanel", "send-page");
//        model.addAttribute("notifyListData", orgNotifySvc.getAll());
        return "pages/organizer/dashboard";
    }

    @GetMapping("/countMembers")
    @ResponseBody
    public Integer countMembers(@RequestParam String targetId){

        if ("event-1".equals(targetId)){
            return 856; // 假資料: 維也納之夜的人數
        } else if ("event-2".equals(targetId)){
            return 234; // 假資料: 弦樂四重奏的人數
        } else if ("prod-1".equals(targetId)){
            return 156; // 假資料:  官方周邊T-SHIRT
        } else if ("prod-2".equals(targetId)){
            return 89; // 假資料: 限定版海報組
        }

        //        if (targetId == null || !targetId.contains("-")) return 0;
//        String[] parts = targetId.split("-");
//        String type = parts[0]; // "event" 或 "prod"
//        Integer id = Integer.valueOf(parts[1]);
//
//        // 呼叫您剛剛在 Service 寫的動態查詢方法
//        return orgNotifySvc.countTargetMembers(type, id);
        return (int)(Math.random()*100);  // 不是以上活動, 回傳隨機數字
    }

    @GetMapping("/getOrganizerNotify")
    public String getOrganizerNotify (
        @RequestParam String organizerId, ModelMap model){
        List<OrganizerNotifyVO> list = orgNotifySvc.getByOrgId(Integer.valueOf(organizerId));
        model.addAttribute("notifyListData", list);
        model.addAttribute("targetPanel", "notifications"); // 讓通知中心亮起

        model.addAttribute("organizerNotifyVO", new OrganizerNotifyVO());
        OrganizerVO organizer = new OrganizerVO();
        organizer.setOrganizerId(Integer.valueOf(organizerId));
        organizer.setName("測試主辦方");
        model.addAttribute("organizer", organizer);

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
