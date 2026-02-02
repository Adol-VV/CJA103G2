package com.momento.linepay.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private BigDecimal amount;
    private String currency; // TWD
    private String orderId; // Unique ID for LINE Pay (e.g., UUID or Combined Order IDs)
    private String productName;
    private List<String> databaseOrderIds; // To track which DB orders this payment covers
}
