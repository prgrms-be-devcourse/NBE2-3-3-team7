package com.project.popupmarket.repository

import com.project.popupmarket.dto.payment.RangeDateTO
import com.project.popupmarket.dto.payment.ReceiptsInfoTO
import com.project.popupmarket.dto.payment.ReservationTO
import com.project.popupmarket.entity.Receipts

interface ReceiptsQueryDslRepository {
    fun reservationDateCheck(reservation: ReservationTO): Boolean
    fun getReceiptsByLandId(landId: Long): List<ReceiptsInfoTO>
    fun getReceiptsByCustomerId(customerId: Long): List<ReceiptsInfoTO>
    fun findReceiptsByOrderId(orderId: String): Receipts?
    fun getRangeDates(landId: Long): List<RangeDateTO>
    fun updateReservationStatusToCanceledByOrderId(orderId: String): Long
    fun dailyUpdateReservationStatusToLeased(): Long
}