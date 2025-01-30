package com.project.popupmarket.repository

import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.enums.ReservationStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class ReceiptsJDslRepositoryImplTest (
    @Autowired private var repo: ReceiptsRepository
) {
    @BeforeEach
    fun setup() {
        val receipt1 = Receipts(
            paymentKey = "pay-123",
            orderId = "order-001",
            customerId = 1L,
            rentalLandId = 10L,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 5),
            amount = BigDecimal("500000"),
            reservationStatus = ReservationStatus.COMPLETED,
            reservedAt = LocalDateTime.now()
        )

        val receipt2 = Receipts(
            paymentKey = "pay-456",
            orderId = "order-002",
            customerId = 2L,
            rentalLandId = 10L,
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2024, 2, 3),
            amount = BigDecimal("300000"),
            reservationStatus = ReservationStatus.COMPLETED,
            reservedAt = LocalDateTime.now().minusDays(1)
        )

        repo.saveAll(listOf(receipt1, receipt2))
    }

    @Test
    fun getReceiptsByLandId() {
        // given
        val receipts = repo.getReceiptsByLandId(10L)

        // when


        // then
        assertThat(receipts).isNotEmpty
        assertThat(receipts.size).isEqualTo(2) // 두 개의 데이터가 존재해야 함
        assertThat(receipts[0].orderId).isEqualTo("order-001")
        assertThat(receipts[1].orderId).isEqualTo("order-002")
    }
}