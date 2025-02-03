package com.project.popupmarket.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.project.popupmarket.dto.payment.AnalyticsTO
import com.project.popupmarket.dto.payment.RangeDateTO
import com.project.popupmarket.dto.payment.ReceiptsInfoTO
import com.project.popupmarket.dto.payment.ReservationTO
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.entity.RentalLand
import com.project.popupmarket.entity.User
import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.RentalStatus
import com.project.popupmarket.enums.ReservationStatus
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
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

    override fun getReceiptsByLandId(landId: Long, pageable: Pageable): Page<ReceiptsInfoTO> {
        val query = entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .where(path(Receipts::rentalLandId).eq(landId))
                    .orderBy(path(Receipts::reservedAt).desc())
            }, context
        )
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize

        val receipts = query.resultList.map { receipt ->
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

        val totalCount: Long = entityManager.createQuery(
            jpql {
                select(count(entity(Receipts::class)))
                    .from(entity(Receipts::class))
                    .where(path(Receipts::rentalLandId).eq(landId))
            }, context
        ).singleResult.toLong()

        return PageImpl(receipts, pageable, totalCount)
    }

    override fun getCustomerName(customerId: Long): String {
        return entityManager.createQuery(
            jpql {
                select<String>(path(User::name))
                    .from(entity(User::class))
                    .where(path(User::id).eq(customerId))
            }, context
        ).singleResult ?: ""
    }

    override fun getReceiptsByCustomerId(customerId: Long, pageable: Pageable): Page<ReceiptsInfoTO> {
        val query = entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .where(path(Receipts::customerId).eq(customerId))
                    .orderBy(path(Receipts::reservedAt).desc())
            }, context
        )
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize

        val receipts = query.resultList.map { rp ->
            mappedToReceiptsInfoTO(rp)
        }

        val totalCount: Long = entityManager.createQuery(
            jpql {
                select(count(entity(Receipts::class)))
                    .from(entity(Receipts::class))
                    .where(path(Receipts::customerId).eq(customerId))
            }, context
        ).singleResult.toLong()

        return PageImpl(receipts, pageable, totalCount)
    }

    override fun getReceiptsByCustomerIdWithLimit(customerId: Long): List<ReceiptsInfoTO> {
        return entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .where(path(Receipts::customerId).eq(customerId))
                    .orderBy(path(Receipts::reservedAt).desc())
            }, context
        ).setMaxResults(10)
            .resultList.map { rp ->
                mappedToReceiptsInfoTO(rp)
            }
    }

    private fun mappedToReceiptsInfoTO(rp: Receipts): ReceiptsInfoTO {
        val landTitle = getLandTitle(rp.rentalLandId)
        val daysBetween = ChronoUnit.DAYS.between(rp.startDate, rp.endDate) + 1
        val price = if (daysBetween > 0) {
            rp.amount.divide(BigDecimal.valueOf(daysBetween), 0, RoundingMode.UP)
        } else BigDecimal.ZERO

        return ReceiptsInfoTO(
            orderId = rp.orderId,
            status = rp.reservationStatus.desc,
            landTitle = landTitle,
            customerName = "",
            amount = rp.amount,
            price = price,
            start = rp.startDate,
            end = rp.endDate
        )
    }

    override fun getMonthlyAnalytics(
        landlordId: Long, startDate: LocalDateTime, endDate: LocalDateTime,
    ): List<AnalyticsTO> {
        val analyticsTO: List<AnalyticsTO> = entityManager.createQuery(
            jpql {
                selectNew<AnalyticsTO>(
                    path(Receipts::rentalLandId),
                    path(RentalLand::title),
                    sum(path(Receipts::amount)),
                )
                    .from(
                        entity(Receipts::class),
                        join(entity(RentalLand::class))
                            .on(path(Receipts::rentalLandId).eq(path(RentalLand::id)))
                    ).whereAnd(
                        path(RentalLand::landlordId).eq(landlordId),
                        path(Receipts::reservedAt).between(startDate, endDate)
                    ).groupBy(
                        path(Receipts::rentalLandId),
                        path(RentalLand::title)
                    )
            }, context
        ).resultList
            .map {
                it.status = when {
                    !isLandActive(it.landId) -> RentalStatus.INACTIVE.desc
                    isCurrentlyRented(it.landId) -> RentalStatus.RENTED.desc
                    else -> RentalStatus.WAITING.desc
                }
                it
            }

        return analyticsTO
    }

    fun isCurrentlyRented(landId: Long): Boolean {
        return entityManager.createQuery(
            jpql {
                select<Long>(count(path(Receipts::paymentKey)))
                    .from(entity(Receipts::class))
                    .whereAnd(
                        path(Receipts::rentalLandId).eq(landId),
                        path(Receipts::startDate).le(LocalDate.now()),
                        path(Receipts::endDate).ge(LocalDate.now())
                    )
            }, context
        ).singleResult > 0
    }

    fun isLandActive(landId: Long): Boolean {
        return entityManager.createQuery(
            jpql {
                select<ActivateStatus>(path(RentalLand::status))
                    .from(entity(RentalLand::class))
                    .where(path(RentalLand::id).eq(landId))
            }, context
        ).singleResult == ActivateStatus.ACTIVE
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
