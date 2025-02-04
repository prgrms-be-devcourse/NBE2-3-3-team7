package com.project.popupmarket.service.receipts

import com.project.popupmarket.dto.payment.*
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.entity.StagingPayment
import com.project.popupmarket.enums.ReservationStatus
import com.project.popupmarket.exception.custom.PaymentException
import com.project.popupmarket.repository.ReceiptsRepository
import com.project.popupmarket.repository.RentalLandJpaRepository
import com.project.popupmarket.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class PaymentService(
    private val stagingPaymentRedisService: StagingPaymentRedisService,
    private val receiptsRepository: ReceiptsRepository,
    private val tossRequestService: TossRequestService,
    private val userRepository: UserRepository,
    private val rentalLandJpaRepository: RentalLandJpaRepository,
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    @Transactional
    fun paymentProcess(payment: TossPaymentTO) {
        val response = tossRequestService.requestPayment(payment)

        try {
            insertReceipt(response)
        } catch (e: Exception) {
            tossRequestService.cancelPayment(payment.paymentKey, "시스템 에러로 인한 결제 취소")
            log.error("영수증 저장 실패로 결제 취소 처리: {}", e.message)
            throw PaymentException("결제 실패: 영수증 저장 중 오류 발생", e)
        }
    }

    fun getPaymentInfo(reservation: ReservationTO): ReservationInfoResponse {
        if (!receiptsRepository.reservationDateCheck(reservation)) {
            throw IllegalArgumentException("이미 예약된 날짜입니다.")
        }

        val user = userRepository.findById(reservation.customerId).orElseThrow {
            throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
        }

        val land = rentalLandJpaRepository.findById(reservation.landId).orElseThrow {
            throw IllegalArgumentException("임대지를 찾을 수 없습니다.")
        }

        return ReservationInfoResponse(
            customerId = user.id!!,
            customerKey = UUID.randomUUID().toString(),
            landTitle = land.title!!,
            price = land.price!!,
            customerEmail = user.email,
            customerName = user.name,
            customerTel = user.tel,
            zipcode = land.zipcode!!,
            address = land.address!!,
            addrDetail = land.addrDetail!!,
        )
    }

    @Transactional
    fun insertStagingPayment(request: StagingRequest) {
        val payment = StagingPayment(
            orderId = request.orderId,
            customerId = request.customerId,
            landId = request.landId,
            startDate = request.start,
            endDate = request.end,
            totalAmount = request.amount
        )

        stagingPaymentRedisService.save(payment.orderId, payment)

        val saved = stagingPaymentRedisService.find(payment.orderId)
        if (saved == null || saved.orderId != payment.orderId) {
            log.error("Redis 저장 실패: Order ID = {}", payment.orderId)
            throw PaymentException("Redis에 결제 데이터를 저장하는 데 실패하였습니다.")
        }
    }

    @Transactional
    fun insertReceipt(payment: TossPaymentTO) {
        val redis = stagingPaymentRedisService.find(payment.orderId)
            ?: throw IllegalArgumentException("해당 Order ID에 대한 StagingPayment를 찾을 수 없습니다.")

        val receipts = Receipts(
            paymentKey = payment.paymentKey,
            orderId = redis.orderId,
            customerId = redis.customerId,
            rentalLandId = redis.landId,
            startDate = redis.startDate,
            endDate = redis.endDate,
            amount = redis.totalAmount,
            reservationStatus = ReservationStatus.COMPLETED
        )

        receiptsRepository.save(receipts)
        stagingPaymentRedisService.delete(redis.orderId)
    }

    @Transactional
    fun deleteStagingPayment(orderId: String) {
        try {
            val staging = stagingPaymentRedisService.find(orderId)
                ?: throw IllegalArgumentException("주문 번호를 찾을 수 없습니다. $orderId")

            stagingPaymentRedisService.delete(staging.orderId)
        } catch (e: IllegalArgumentException) {
            log.warn("해당 임시 결제 내역을 삭제할 수 없습니다. : {}", e.message)
            throw e
        } catch (e: Exception) {
            log.error("임시 결제 내역을 삭제하면서 알 수 없는 오류가 발생했습니다.")
            throw PaymentException("임시 결제 내역 삭제에 실패했습니다.")
        }
    }

    fun getReceiptsInfoByLandId(landId: Long, pageable: Pageable): Page<ReceiptsInfoTO> {
        return receiptsRepository.getReceiptsByLandId(landId, pageable)
    }

    fun getReceiptsInfoByCustomerId(customerId: Long, pageable: Pageable): Page<ReceiptsInfoTO> {
        return receiptsRepository.getReceiptsByCustomerId(customerId, pageable)
    }

    fun getReceiptsInfoByCustomerIdWithLimit(customerId: Long): List<ReceiptsInfoTO> {
        return receiptsRepository.getReceiptsByCustomerIdWithLimit(customerId)
    }

    @Transactional
    fun changeReservationStatus(orderId: String) {
        val receipts = receiptsRepository.findReceiptsByOrderId(orderId)
            ?: throw IllegalArgumentException("해당 주문 번호에 대한 예약 정보를 찾을 수 없습니다.")

        val affected = receiptsRepository.updateReservationStatusToCanceledByOrderId(orderId)
        if (affected == 0L) {
            throw PaymentException("")
        }

        val payment = TossPaymentTO(
            paymentKey = receipts.paymentKey,
            orderId = receipts.orderId,
            totalAmount = receipts.amount
        )

        tossRequestService.cancelPayment(payment.paymentKey, "시스템 에러로 인한 결제 취소")
    }

    fun getMonthlyAnalytics(landlordId: Long) :List<AnalyticsTO> {
        val endDate = LocalDate.now().atStartOfDay()
        val startDate = endDate.minusDays(30)

        return receiptsRepository.getMonthlyAnalytics(landlordId, startDate, endDate)
    }

    fun getRangeDates(rentalPlaceSeq: Long): List<RangeDateTO> {
        return receiptsRepository.getRangeDates(rentalPlaceSeq)
    }
}