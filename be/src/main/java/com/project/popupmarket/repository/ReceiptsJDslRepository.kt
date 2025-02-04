package com.project.popupmarket.repository

import com.project.popupmarket.dto.admin.AdminDashboardTO
import com.project.popupmarket.dto.payment.AnalyticsTO
import com.project.popupmarket.dto.payment.RangeDateTO
import com.project.popupmarket.dto.payment.ReceiptsInfoTO
import com.project.popupmarket.dto.payment.ReservationTO
import com.project.popupmarket.entity.Receipts
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface ReceiptsJDslRepository {
    fun reservationDateCheck(reservation: ReservationTO): Boolean
    fun getReceiptsByLandId(landId: Long, pageable: Pageable): Page<ReceiptsInfoTO>
    fun getReceiptsByCustomerId(customerId: Long, pageable: Pageable): Page<ReceiptsInfoTO>
    fun getReceiptsByCustomerIdWithLimit(customerId: Long): List<ReceiptsInfoTO>
    fun getMonthlyAnalytics(landlordId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<AnalyticsTO>
    fun findReceiptsByOrderId(orderId: String): Receipts?
    fun getRangeDates(landId: Long): List<RangeDateTO>
    fun updateReservationStatusToCanceledByOrderId(orderId: String): Long
    fun dailyUpdateReservationStatusToLeased(): Long
    fun getCustomerName(customerId: Long): String
    fun getLandTitle(landId: Long): String
}