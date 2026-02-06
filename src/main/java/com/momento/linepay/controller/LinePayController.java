package com.momento.linepay.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.linepay.dto.PaymentRequest;
import com.momento.linepay.service.LinePayService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/linepay")
public class LinePayController {

    @Autowired
    private LinePayService linePayService;

    @PostMapping("/request")
    @ResponseBody
    public ResponseEntity<String> requestPayment(@RequestBody PaymentRequest request, HttpSession session,
            HttpServletRequest httpServletRequest) {
        try {
            // Generate a unique transaction order ID for LINE Pay
            String transactionOrderId = UUID.randomUUID().toString();
            request.setOrderId(transactionOrderId);
            request.setProductName("Momento Order"); // Generic name or combined

            // Store the actual DB order IDs in session mapped by transactionOrderId
            if (request.getDatabaseOrderIds() != null && !request.getDatabaseOrderIds().isEmpty()) {
                session.setAttribute(transactionOrderId, request.getDatabaseOrderIds());
            }
            session.setAttribute(transactionOrderId + "_amount", request.getAmount());

            // Construct dynamic baseUrl
            String scheme = httpServletRequest.getScheme();
            String serverName = httpServletRequest.getServerName();
            int serverPort = httpServletRequest.getServerPort();
            String contextPath = httpServletRequest.getContextPath();

            StringBuilder url = new StringBuilder();
            url.append(scheme).append("://").append(serverName);
            if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
                url.append(":").append(serverPort);
            }
            url.append(contextPath);
            String baseUrl = url.toString();

            String paymentUrl = linePayService.initiatePayment(request, baseUrl);
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Payment initiation failed: " + e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public String confirmPayment(
            @RequestParam("transactionId") String transactionId,
            @RequestParam("orderId") String orderId,
            HttpSession session,
            HttpServletResponse response) {
        try {
            // Retrieve DB order IDs (Optional: for status update)
            @SuppressWarnings("unchecked")
            List<String> dbOrderIds = (List<String>) session.getAttribute(orderId);

            // For now, checks amount? The confirm API needs amount.
            // We don't have the amount here unless we stored it in session too.
            // LINE Pay confirm requires amount to match the authorized amount.
            // Let's store amount in session or re-calculate?
            // Re-calculating is hard without querying DB.
            // Let's store amount in session too.

            // Wait, we need the amount.
            // Let's modify requestPayment to store amount.

            // Quick fix: Retrieve amount from session or trust the flow?
            // Correct way: Store TransactionData object in session.

            // To unlock "success" flow, we just need the LINE Pay Confirm to pass.
            // But we need the amount to call Confirm.

            // If we cannot get amount, we might fail.
            // Let's adjust /request to store amount.

            // Assuming we stored it in a Map or object.
            BigDecimal amount = (BigDecimal) session.getAttribute(orderId + "_amount");
            if (amount == null) {
                // Fallback or error.
                // For this implementation, let's assume valid flow.
                // If null, maybe we can't confirm.
                return "redirect:/member/checkout?payment=error";
            }

            boolean isSuccess = linePayService.confirmPayment(transactionId, amount);
            if (isSuccess) {
                // Cleanup session
                session.removeAttribute(orderId);
                session.removeAttribute(orderId + "_amount");

                return "redirect:/member/checkout?payment=success";
            } else {
                return "redirect:/member/checkout?payment=fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/member/checkout?payment=error";
        }
    }

    @GetMapping("/cancel")
    public String cancelPayment() {
        return "redirect:/member/checkout?payment=cancel";
    }
}
