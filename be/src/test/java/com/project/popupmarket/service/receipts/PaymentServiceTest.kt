package com.project.popupmarket.service.receipts

import com.project.popupmarket.dto.payment.ReceiptsTO
import com.project.popupmarket.entity.StagingPayment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
class PaymentServiceTest {

//    @Autowired
//    private lateinit var paymentService: PaymentService
//
//    @Autowired
//    private lateinit var stagingPaymentRedisService: StagingPaymentRedisService
//
//    @Autowired
//    private lateinit var redisTemplate: RedisTemplate<String, Any>
//
//    @AfterEach
//    fun clearRedisData() {
//        // 특정 패턴의 모든 키 삭제
//        val keys: Set<String>? = redisTemplate.keys("staging:*")
//        if (!keys.isNullOrEmpty()) {
//            redisTemplate.delete(keys)
//        }
//    }
//
//    @Test
//    @DisplayName("insertStagingPayment 성공 테스트")
//    fun testInsertStagingPayment_Success() {
//        // Given
//        val orderId = UUID.randomUUID().toString()
//        val receipt = ReceiptsTO(
//            paymentKey = "123",
//            orderId = orderId,
//            customerId = 1L,
//            landId = 1L,
//            start = LocalDate.of(2025, 2, 14),
//            end = LocalDate.of(2025, 2, 20),
//            amount = BigDecimal.valueOf(100000)
//        )
//
//        // When
//        val stagingPayment = StagingPayment(
//            orderId = receipt.orderId,
//            customerId = receipt.customerId,
//            rentalLandId = receipt.landId,
//            startDate = receipt.start,
//            endDate = receipt.end,
//            totalAmount = receipt.amount
//        )
//        paymentService.insertStagingPayment(receipt)
//
//        // Then
//        println(stagingPayment.toString())
//        assertEquals(stagingPayment.orderId, stagingPaymentRedisService.find(orderId)?.orderId)
//        assertEquals(stagingPayment.startDate, stagingPaymentRedisService.find(orderId)?.startDate)
//    }
//
//    @Test
//    @DisplayName("deleteStagingPayment 성공 테스트")
//    fun testDeleteStagingPayment_Success() {
//        // Given
//        val orderId = UUID.randomUUID().toString()
//        val receipt = ReceiptsTO(
//            paymentKey = "123",
//            orderId = orderId,
//            customerId = 1L,
//            landId = 1L,
//            start = LocalDate.of(2025, 2, 14),
//            end = LocalDate.of(2025, 2, 20),
//            amount = BigDecimal.valueOf(100000)
//        )
//
//        paymentService.insertStagingPayment(receipt)
//
//        // When
//        paymentService.deleteStagingPayment(receipt)
//
//        // Then
//        assertNull(stagingPaymentRedisService.find(orderId))
//    }
}