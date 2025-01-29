package com.project.popupmarket.service.receipts

import com.project.popupmarket.dto.payment.*
import com.project.popupmarket.entity.QRentalLand
import com.project.popupmarket.entity.QUser
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.entity.StagingPayment
import com.project.popupmarket.enums.ReservationStatus
import com.project.popupmarket.exception.custom.PaymentException
import com.project.popupmarket.repository.ReceiptsQueryDslRepositoryImpl
import com.project.popupmarket.repository.ReceiptsRepository
import com.project.popupmarket.util.UserContextUtil
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class PaymentService(
    private val stagingPaymentRedisService: StagingPaymentRedisService,
    private val receiptsRepository: ReceiptsRepository,
    private val tossRequestService: TossRequestService,
    private val userContextUtil: UserContextUtil,
    private val queryFactory: JPAQueryFactory,
    private val receiptsQueryDslRepositoryImpl: ReceiptsQueryDslRepositoryImpl
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    @Transactional
    fun paymentProcess(payment: TossPaymentTO) {
        val receipt = tossRequestService.requestPayment(payment)
        receipt.customerId = userContextUtil.getUserId()

        try {
            insertReceipt(receipt)
        } catch (e: Exception) {
            tossRequestService.cancelPayment(payment.paymentKey, "시스템 에러로 인한 결제 취소")
            log.error("영수증 저장 실패로 결제 취소 처리: {}", e.message)
            throw PaymentException("결제 실패: 영수증 저장 중 오류 발생", e)
        }
    }

    fun getPaymentInfo(reservation: ReservationTO): ReservationInfoResponse {
        val isReservationValid = receiptsRepository.reservationDateCheck(reservation)
        if (!isReservationValid) {
            throw IllegalArgumentException("이미 예약된 날짜입니다.")
        }

        val user = queryFactory.selectFrom(QUser.user)
            .where(QUser.user.id.eq(reservation.customerId))
            .fetchOne()

        val rentalLand = queryFactory.selectFrom(QRentalLand.rentalLand)
            .where(QRentalLand.rentalLand.id.eq(reservation.landId))
            .fetchOne()

        if (user == null || rentalLand == null) {
            throw IllegalArgumentException("사용자 또는 임대지를 찾을 수 없습니다.")
        }

        return ReservationInfoResponse(
            customerKey = UUID.randomUUID().toString(),
//            landTitle = rentalLand.title,
//            price = rentalLand.price,
//            customerEmail = user.email,
//            customerName = user.name,
//            customerTel = user.tel,
//            zipcode = rentalLand.zipcode,
//            address = rentalLand.address,
//            addrDetail = rentalLand.addrDetail 3
            landTitle = "임대지1",
            price = BigDecimal.valueOf(10000),
            customerEmail = "tto0113@gmail.com",
            customerName = "홍길동",
            customerTel = "010-5672-3635",
            zipcode = "12345",
            address = "충북 청주시 청원구",
            addrDetail = "상세주소"
        )
    }

    @Transactional
    fun insertStagingPayment(receipt: ReceiptsTO) {
        val stagingPayment = StagingPayment(
            orderId = receipt.orderId,
            customerId = receipt.customerId,
            rentalLandId = receipt.landId,
            startDate = receipt.start,
            endDate = receipt.end,
            totalAmount = receipt.amount
        )

        stagingPaymentRedisService.save(stagingPayment.orderId, stagingPayment)

        val saved = stagingPaymentRedisService.find(stagingPayment.orderId)
        if (saved == null || saved.orderId != stagingPayment.orderId) {
            log.error("Redis 저장 실패: Order ID = {}", stagingPayment.orderId)
            throw PaymentException("Redis에 결제 데이터를 저장하는 데 실패하였습니다.")
        }
    }

    @Transactional
    fun insertReceipt(receipt: ReceiptsTO) {
        stagingPaymentRedisService.find(receipt.orderId)
            ?: throw IllegalArgumentException("해당 Order ID에 대한 StagingPayment를 찾을 수 없습니다.")

        val receipts = Receipts(
            paymentKey = receipt.paymentKey,
            orderId = receipt.orderId,
            customerId = receipt.customerId,
            rentalLandId = receipt.landId,
            startDate = receipt.start,
            endDate = receipt.end,
            amount = receipt.amount,
            reservationStatus = ReservationStatus.COMPLETED
        )

        receiptsRepository.save(receipts)
        stagingPaymentRedisService.delete(receipt.orderId)
    }

    @Transactional
    fun deleteStagingPayment(receipt: ReceiptsTO) {
        try {
            val stagingPayment = stagingPaymentRedisService.find(receipt.orderId)
                ?: throw IllegalArgumentException("주문 번호를 찾을 수 없습니다. ${receipt.orderId}")

            stagingPaymentRedisService.delete(receipt.orderId)
        } catch (e: IllegalArgumentException) {
            log.warn("해당 임시 결제 내역을 삭제할 수 없습니다. : {}", e.message)
            throw e
        } catch (e: Exception) {
            log.error("임시 결제 내역을 삭제하면서 알 수 없는 오류가 발생했습니다.")
            throw PaymentException("임시 결제 내역 삭제에 실패했습니다.")
        }
    }

    fun getReceiptsInfoByLandId(landId: Long): List<ReceiptsInfoTO> {
        return receiptsQueryDslRepositoryImpl.getReceiptsByLandId(landId)
    }

    fun getReceiptsInfoByCustomerId(customerId: Long): List<ReceiptsInfoTO> {
        return receiptsQueryDslRepositoryImpl.getReceiptsByCustomerId(customerId)
    }

    @Transactional
    fun changeReservationStatus(orderId: String) {
        val receipts = receiptsQueryDslRepositoryImpl.findReceiptsByOrderId(orderId)
            ?: throw IllegalArgumentException("해당 주문 번호에 대한 예약 정보를 찾을 수 없습니다.")

        val affected = receiptsRepository.updateReservationStatusToCanceledByOrderId(orderId)
        if (affected == 0L) {
            throw PaymentException("")
        }

        val payment = TossPaymentTO(
            paymentKey = receipts.paymentKey,
            orderId = receipts.orderId,
            amount = receipts.amount
        )

        tossRequestService.cancelPayment(payment.paymentKey, "시스템 에러로 인한 결제 취소")
    }

    fun getRangeDates(rentalPlaceSeq: Long): List<RangeDateTO> {
        return receiptsQueryDslRepositoryImpl.getRangeDates(rentalPlaceSeq)
    }
}