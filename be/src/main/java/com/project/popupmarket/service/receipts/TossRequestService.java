package com.project.popupmarket.service.receipts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.popupmarket.config.TossPaymentConfig;
import com.project.popupmarket.dto.payment.ReceiptsTO;
import com.project.popupmarket.dto.payment.TossPaymentTO;
import com.project.popupmarket.exception.custom.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossRequestService {

    private final TossPaymentConfig tossPaymentConfig;
    private final ObjectMapper objectMapper;

    public ReceiptsTO requestPayment(TossPaymentTO payment) {
        String encodeKey = Base64.getEncoder()
                .encodeToString((tossPaymentConfig.getTestSecreteKey() + ":").getBytes(StandardCharsets.UTF_8));

        try {
            // 요청 바디 생성
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("paymentKey", payment.getPaymentKey());
            requestBody.put("amount", payment.getAmount());
            requestBody.put("orderId", payment.getOrderId());

            // HTTP 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TossPaymentConfig.TOSS_URL + "confirm"))
                    .header("Authorization", "Basic " + encodeKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();

            // 요청 실행 및 응답 처리
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response == null || response.statusCode() != 200) {
                throw new PaymentException("결제 승인 실패: " + (response != null ? response.body() : "응답 없음"));
            }

            // 응답을 ReceiptsTO로 변환
            return objectMapper.readValue(response.body(), ReceiptsTO.class);

        } catch (IOException | InterruptedException e) {
            log.error("결제 요청 중 오류 발생: {}", e.getMessage());
            throw new PaymentException("결제 요청 중 오류가 발생했습니다.", e);
        }
    }

    public HttpResponse<String> cancelPayment(String paymentKey, String message) {
        String encodeKey = Base64.getEncoder()
                .encodeToString((tossPaymentConfig.getTestSecreteKey() + ":").getBytes(StandardCharsets.UTF_8));

        try {
            // 요청 바디 생성
            String requestBody = objectMapper.writeValueAsString(Map.of("cancelReason", message));

            // HTTP 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TossPaymentConfig.TOSS_URL + paymentKey + "/cancel"))
                    .header("Authorization", "Basic " + encodeKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 요청 전송 및 상태 코드 확인
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("결제 취소 실패: HTTP 상태 코드 {}", response.statusCode());
                throw new RuntimeException("결제 취소 실패: 상태 코드 " + response.statusCode());
            }
            return response;
        } catch (IOException | InterruptedException e) {
            log.error("결제 취소 요청 실패: {}", e.getMessage());
            throw new RuntimeException("결제 취소 요청 중 오류가 발생했습니다.", e);
        }
    }
}
