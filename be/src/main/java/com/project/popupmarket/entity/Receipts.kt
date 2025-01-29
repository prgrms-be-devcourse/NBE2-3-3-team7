package com.project.popupmarket.entity

import com.project.popupmarket.enums.ReservationStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "receipts")
class Receipts() {
    @Id
    @Column(name = "payment_key", nullable = false)
    var paymentKey: String = ""

    @Column(name = "order_id", nullable = false)
    var orderId: String = ""

    @Column(name = "customer_id", nullable = false)
    var customerId: Long = 0L

    @Column(name = "rental_land_id", nullable = false)
    var rentalLandId: Long = 0L

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate = LocalDate.now()

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate = LocalDate.now()

    @Column(name = "amount", precision = 10)
    var amount: BigDecimal = BigDecimal.ZERO

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    var reservationStatus: ReservationStatus = ReservationStatus.COMPLETED

    @Column(name = "reserved_at", nullable = false, updatable = false)
    var reservedAt: LocalDateTime = LocalDateTime.now()

    constructor(
        paymentKey: String,
        orderId: String,
        customerId: Long,
        rentalLandId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        amount: BigDecimal,
        reservationStatus: ReservationStatus,
        reservedAt: LocalDateTime = LocalDateTime.now()
    ) : this() {
        this.paymentKey = paymentKey
        this.orderId = orderId
        this.customerId = customerId
        this.rentalLandId = rentalLandId
        this.startDate = startDate
        this.endDate = endDate
        this.amount = amount
        this.reservationStatus = reservationStatus
        this.reservedAt = reservedAt
    }
}