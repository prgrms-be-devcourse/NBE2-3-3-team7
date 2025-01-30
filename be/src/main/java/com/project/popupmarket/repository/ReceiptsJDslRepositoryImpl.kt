package com.project.popupmarket.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.project.popupmarket.dto.payment.RangeDateTO
import com.project.popupmarket.dto.payment.ReceiptsInfoTO
import com.project.popupmarket.dto.payment.ReservationTO
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.entity.RentalLand
import com.project.popupmarket.enums.ReservationStatus
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Repository
class ReceiptsJDslRepositoryImpl(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) : ReceiptsJDslRepository {

    override fun reservationDateCheck(reservation: ReservationTO): Boolean {
        return entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .whereAnd(
                        path(Receipts::rentalLandId).equal(reservation.landId),
                        path(Receipts::startDate).lessThanOrEqualTo(reservation.end),
                        path(Receipts::endDate).greaterThanOrEqualTo(reservation.start),
                        path(Receipts::reservationStatus).notEqual(ReservationStatus.CANCELED)
                    )
            }, context
        ).resultList.isEmpty()
    }

    override fun getReceiptsByLandId(landId: Long): List<ReceiptsInfoTO> {
        return entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .whereAnd(
                        path(Receipts::rentalLandId).eq(landId),
                    )
                    .orderBy(path(Receipts::reservedAt).desc())
            }, context
        ).resultList.map { receipt ->
            val customerName = getCustomerName(receipt.customerId)

            val daysBetween = ChronoUnit.DAYS.between(receipt.startDate, receipt.endDate) + 1
            val price = if (daysBetween > 0) {
                receipt.amount.divide(BigDecimal.valueOf(daysBetween), 0, RoundingMode.UP)
            } else BigDecimal.ZERO

            ReceiptsInfoTO(
                orderId = receipt.orderId,
                status = receipt.reservationStatus.desc,
                landTitle = "",
                customerName = customerName,
                amount = receipt.amount,
                price = price,
                start = receipt.startDate,
                end = receipt.endDate
            )
        }
    }

    // TODO : 사용자 엔티티 Migration 이후에 수정
    override fun getCustomerName(customerId: Long): String {
        return "John Doe"
    }

    override fun getReceiptsByCustomerId(customerId: Long): List<ReceiptsInfoTO> {
        return entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .whereAnd(
                        path(Receipts::customerId).eq(customerId),
                    )
                    .orderBy(path(Receipts::reservedAt).desc())
            }, context
        ).resultList.map { receipt ->
            val landTitle = getLandTitle(receipt.rentalLandId)

            val daysBetween = ChronoUnit.DAYS.between(receipt.startDate, receipt.endDate) + 1
            val price = if (daysBetween > 0) {
                receipt.amount.divide(BigDecimal.valueOf(daysBetween), 0, RoundingMode.UP)
            } else BigDecimal.ZERO

            ReceiptsInfoTO(
                orderId = receipt.orderId,
                status = receipt.reservationStatus.desc,
                landTitle = landTitle,
                customerName = "",
                amount = receipt.amount,
                price = price,
                start = receipt.startDate,
                end = receipt.endDate
            )
        }
    }

    override fun getLandTitle(landId: Long): String {
        return entityManager.createQuery(
            jpql {
                select<String>(path(RentalLand::title))
                    .from(entity(RentalLand::class))
                    .where(path(RentalLand::id).eq(landId))
            }, context
        ).singleResult ?: ""
    }

    override fun findReceiptsByOrderId(orderId: String): Receipts? {
        return entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .where(path(Receipts::orderId).eq(orderId))
            }, context
        ).singleResult
    }

    override fun getRangeDates(landId: Long): List<RangeDateTO> {
        return entityManager.createQuery(
            jpql {
                select<RangeDateTO>(
                    path(Receipts::startDate),
                    path(Receipts::endDate)
                )
                    .from(entity(Receipts::class))
                    .whereAnd(
                        path(Receipts::rentalLandId).eq(landId),
                        path(Receipts::reservationStatus).eq(ReservationStatus.COMPLETED),
                        path(Receipts::startDate).greaterThanOrEqualTo(LocalDate.now())
                    )
            }, context
        ).resultList
//            .map { (startDate, endDate) -> RangeDateTO(startDate, endDate) }
    }

    override fun updateReservationStatusToCanceledByOrderId(orderId: String): Long {
        return entityManager.createQuery(
            jpql {
                update(entity(Receipts::class))
                    .set(path(Receipts::reservationStatus), ReservationStatus.CANCELED)
                    .where(path(Receipts::orderId).eq(orderId))
            }, context
        ).executeUpdate().toLong()
    }

    override fun dailyUpdateReservationStatusToLeased(): Long {
        return entityManager.createQuery(
            jpql {
                update(entity(Receipts::class))
                    .set(path(Receipts::reservationStatus), ReservationStatus.LEASED)
                    .whereAnd(
                        path(Receipts::startDate).greaterThanOrEqualTo(LocalDate.now()),
                        path(Receipts::reservationStatus).eq(ReservationStatus.COMPLETED)
                    )
            }, context
        ).executeUpdate().toLong()
    }
}
