package com.momento.linepay.service;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.momento.linepay.dto.PaymentRequest;

@Service
public class LinePayService {

    @Value("${line.pay.channel-id}")
    private String channelId;

    @Value("${line.pay.channel-secret}")
    private String channelSecret;

    @Value("${line.pay.api-url}")
    private String apiUrl;

    @Value("${line.pay.confirm-url}")
    private String confirmUrl;

    @Value("${line.pay.cancel-url}")
    private String cancelUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String initiatePayment(PaymentRequest request) throws Exception {
        String requestUri = "/v3/payments/request";
        String nonce = UUID.randomUUID().toString();

        // Prepare Request Body
        Map<String, Object> body = new HashMap<>();
        body.put("amount", request.getAmount());
        body.put("currency", "TWD");
        body.put("orderId", request.getOrderId());

        Map<String, Object> redirectUrls = new HashMap<>();
        // Append context info to confirm URL if needed, or store in session.
        // Here we keep it simple.
        redirectUrls.put("confirmUrl", confirmUrl + "?orderId=" + request.getOrderId());
        redirectUrls.put("cancelUrl", cancelUrl);
        body.put("redirectUrls", redirectUrls);

        Map<String, Object> product = new HashMap<>();
        product.put("name", request.getProductName());
        product.put("quantity", 1);
        product.put("price", request.getAmount());

        body.put("packages", Arrays.asList(
                new HashMap<String, Object>() {
                    {
                        put("id", "pkg-1");
                        put("amount", request.getAmount());
                        put("products", Arrays.asList(product));
                    }
                }));

        String requestJson = objectMapper.writeValueAsString(body);

        // Generate Signature
        String signature = encrypt(channelSecret, channelSecret + requestUri + requestJson + nonce);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-LINE-ChannelId", channelId);
        headers.set("X-LINE-Authorization-Nonce", nonce);
        headers.set("X-LINE-Authorization", signature);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        // Call API
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + requestUri,
                HttpMethod.POST,
                entity,
                String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        if ("0000".equals(root.path("returnCode").asText())) {
            return root.path("info").path("paymentUrl").path("web").asText();
        } else {
            throw new RuntimeException("LINE Pay Error: " + root.path("returnMessage").asText());
        }
    }

    public boolean confirmPayment(String transactionId, BigDecimal amount) throws Exception {
        String requestUri = "/v3/payments/" + transactionId + "/confirm";
        String nonce = UUID.randomUUID().toString();

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("currency", "TWD");

        String requestJson = objectMapper.writeValueAsString(body);
        String signature = encrypt(channelSecret, channelSecret + requestUri + requestJson + nonce);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-LINE-ChannelId", channelId);
        headers.set("X-LINE-Authorization-Nonce", nonce);
        headers.set("X-LINE-Authorization", signature);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + requestUri,
                HttpMethod.POST,
                entity,
                String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        return "0000".equals(root.path("returnCode").asText());
    }

    private static String encrypt(String keys, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(keys.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return java.util.Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
