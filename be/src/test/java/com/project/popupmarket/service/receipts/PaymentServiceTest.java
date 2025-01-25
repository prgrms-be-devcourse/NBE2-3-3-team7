package com.project.popupmarket.service.receipts;

import com.project.popupmarket.config.RedisConfig;
import com.project.popupmarket.dto.payment.ReceiptsTO;
import com.project.popupmarket.entity.StagingPayment;
import com.project.popupmarket.exception.custom.PaymentException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StagingPaymentRedisService stagingPaymentRedisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void clearRedisData() {
        // 특정 패턴의 모든 키 삭제
        Set<String> keys = redisTemplate.keys("staging:*"); // 키 패턴 지정
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys); // 여러 키 삭제
        }
    }

    @Test
    @DisplayName("insertStagingPayment 성공 테스트")
    void testInsertStagingPayment_Success() {
        // Given
        String orderId = UUID.randomUUID().toString();
        ReceiptsTO receipt = ReceiptsTO.builder()
                .orderId(orderId)
                .customerId(1L)
                .landId(1L)
                .start(LocalDate.of(2025, 2, 14))
                .end(LocalDate.of(2025, 2, 20))
                .amount(BigDecimal.valueOf(100000))
                .build();

        // When
        StagingPayment stagingPayment = StagingPayment.builder()
                .orderId(receipt.getOrderId())
                .customerId(receipt.getCustomerId())
                .rentalLandId(receipt.getLandId())
                .startDate(receipt.getStart())
                .endDate(receipt.getEnd())
                .totalAmount(receipt.getAmount())
                .build();
        paymentService.insertStagingPayment(receipt);

        // Then
        System.out.println(stagingPayment.toString());
        assertEquals(stagingPayment.getOrderId(), stagingPaymentRedisService.find(orderId).getOrderId());
        assertEquals(stagingPayment.getStartDate(), stagingPaymentRedisService.find(orderId).getStartDate());
    }

    @Test
    @DisplayName("deleteStagingPayment 성공 테스트")
    void testDeleteStagingPayment_Success() {
        // Given
        String orderId = UUID.randomUUID().toString();
        ReceiptsTO receipt = ReceiptsTO.builder()
                .orderId(orderId)
                .customerId(1L)
                .landId(1L)
                .start(LocalDate.of(2025, 2, 14))
                .end(LocalDate.of(2025, 2, 20))
                .amount(BigDecimal.valueOf(100000))
                .build();

        paymentService.insertStagingPayment(receipt);

        // When
        paymentService.deleteStagingPayment(receipt);

        // Then
        assertNull(stagingPaymentRedisService.find(orderId));
    }

}