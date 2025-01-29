package com.project.popupmarket.repository

import com.project.popupmarket.dto.payment.RangeDateTO
import com.project.popupmarket.dto.payment.ReceiptsInfoTO
import com.project.popupmarket.dto.payment.ReservationTO
import com.project.popupmarket.entity.QReceipts
import com.project.popupmarket.entity.QRentalLand
import com.project.popupmarket.entity.QUser
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.enums.ReservationStatus
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Repository
class ReceiptsQueryDslRepositoryImpl(
    private val query: JPAQueryFactory
) : ReceiptsQueryDslRepository {

    override fun reservationDateCheck(reservation: ReservationTO): Boolean {
        val qReceipts = QReceipts.receipts

        val result = query
            .select(qReceipts)
            .from(qReceipts)
            .where(
                qReceipts.rentalLandId.eq(reservation.landId)
                    .and(qReceipts.startDate.loe(reservation.end))
                    .and(qReceipts.endDate.goe(reservation.start))
                    .and(qReceipts.reservationStatus.ne(ReservationStatus.CANCELED))
            )
            .fetch()

        return result.isEmpty()
    }

    override fun getReceiptsByLandId(landId: Long): List<ReceiptsInfoTO> {
        val qReceipts = QReceipts.receipts
        val qUser = QUser.user
        val receiptsList = query
            .select(qReceipts)
            .from(qReceipts)
            .where(
                qReceipts.rentalLandId.eq(landId)
                    .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED))
            )
            .orderBy(qReceipts.reservedAt.desc())
            .fetch()

        val receiptsInfoList = mutableListOf<ReceiptsInfoTO>()

        receiptsList.forEach { receipt ->
            val customerName = query
                .select(qUser.name)
                .from(qUser)
                .where(qUser.id.eq(receipt.customerId))
                .fetchOne()

            val daysBetween = ChronoUnit.DAYS.between(receipt.startDate, receipt.endDate) + 1
            val price = if (daysBetween > 0) {
                receipt.amount.divide(BigDecimal.valueOf(daysBetween), 0, RoundingMode.UP)
            } else BigDecimal.ZERO

            val infoTO = ReceiptsInfoTO(
                orderId = receipt.orderId,
                status = receipt.reservationStatus.desc,
                landTitle = "",
                customerName = customerName.toString(),
                amount = receipt.amount,
                price = price,
                start = receipt.startDate,
                end = receipt.endDate
            )
            receiptsInfoList.add(infoTO)
        }
        return receiptsInfoList
    }

    override fun getReceiptsByCustomerId(customerId: Long): List<ReceiptsInfoTO> {
        val qReceipts = QReceipts.receipts
        val qRentalLand = QRentalLand.rentalLand
        val receiptsList = query
            .select(qReceipts)
            .from(qReceipts)
            .where(qReceipts.customerId.eq(customerId))
            .orderBy(qReceipts.reservedAt.desc())
            .fetch()

        val receiptInfoList = mutableListOf<ReceiptsInfoTO>()

        receiptsList.forEach { receipt ->
            val landTitle = query
                .select(qRentalLand.title)
                .from(qRentalLand)
                .where(qRentalLand.id.eq(receipt.rentalLandId))
                .fetchOne()

            val daysBetween = ChronoUnit.DAYS.between(receipt.startDate, receipt.endDate) + 1
            val price = if (daysBetween > 0) {
                receipt.amount.divide(BigDecimal.valueOf(daysBetween), 0, RoundingMode.UP)
            } else BigDecimal.ZERO

            val infoTO = ReceiptsInfoTO(
                orderId = receipt.orderId,
                status = receipt.reservationStatus.desc,
                landTitle = landTitle.toString(),
                customerName = "",
                amount = receipt.amount,
                price = price,
                start = receipt.startDate,
                end = receipt.endDate
            )
            receiptInfoList.add(infoTO)
        }
        return receiptInfoList
    }

    override fun findReceiptsByOrderId(orderId: String): Receipts? {
        val qReceipts = QReceipts.receipts
        return query
            .select(qReceipts)
            .from(qReceipts)
            .where(qReceipts.orderId.eq(orderId))
            .fetchOne()
    }

    override fun getRangeDates(landId: Long): List<RangeDateTO> {
        val qReceipts = QReceipts.receipts
        return query
            .select(Projections.constructor(RangeDateTO::class.java, qReceipts.startDate, qReceipts.endDate))
            .from(qReceipts)
            .where(
                qReceipts.rentalLandId.eq(landId)
                    .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED))
                    .and(qReceipts.startDate.goe(LocalDate.now()))
            )
            .fetch()
    }

    override fun updateReservationStatusToCanceledByOrderId(orderId: String): Long {
        val qReceipts = QReceipts.receipts
        return query
            .update(qReceipts)
            .set(qReceipts.reservationStatus, ReservationStatus.CANCELED)
            .where(qReceipts.orderId.eq(orderId))
            .execute()
    }

    override fun dailyUpdateReservationStatusToLeased(): Long {
        val qReceipts = QReceipts.receipts
        val today = LocalDate.now()

        return query
            .update(qReceipts)
            .set(qReceipts.reservationStatus, ReservationStatus.LEASED)
            .where(
                qReceipts.startDate.goe(today)
                    .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED))
            )
            .execute()
    }
}
