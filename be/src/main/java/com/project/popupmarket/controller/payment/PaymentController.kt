package com.project.popupmarket.controller.payment

import com.project.popupmarket.dto.payment.*
import com.project.popupmarket.service.land.RentalLandService
import com.project.popupmarket.service.receipts.PaymentService
import com.project.popupmarket.util.UserContextUtil
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api")
class PaymentController(
    private val paymentService: PaymentService,
    private val userContextUtil: UserContextUtil,
    private val rentalLandService: RentalLandService
) {

    @GetMapping("/payment")
    @Operation(summary = "임대지 및 예약자 정보 조회")
    fun reservationInfo(
        @RequestParam land: Long,
        @RequestParam start: LocalDate,
        @RequestParam end: LocalDate
    ): ResponseEntity<ReservationInfoResponse> {
        val reservation = ReservationTO(
            customerId = userContextUtil.userId ?: throw IllegalStateException("사용자 ID가 필요합니다"),
            landId = land,
            start = start,
            end = end
        )

        return ResponseEntity.ok(paymentService.getPaymentInfo(reservation))
    }

    @PostMapping("/payment")
    @Operation(summary = "임시 결제 내역 추가")
    fun payment(@RequestBody request: StagingRequest): ResponseEntity<Void> {
        val customerId = userContextUtil.userId

        if (request.customerId != customerId) {
            throw IllegalStateException("사용자 정보가 인증되지 않았습니다.")
        }

        paymentService.insertStagingPayment(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/payment/success")
    @Operation(summary = "결제 승인 요청 및 영수증 추가")
    fun paymentSuccess(@RequestBody payment: TossPaymentTO): ResponseEntity<Void> {
        paymentService.paymentProcess(payment)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/payment/fail")
    @Operation(summary = "결제 실패, 임시 결제 내역 삭제")
    fun paymentFail(@RequestBody request: PaymentFailRequest): ResponseEntity<String> {
        paymentService.deleteStagingPayment(request.orderId)
        return ResponseEntity.ok("success")
    }

    @GetMapping("/receipt")
    @Operation(summary = "사용자 결제 내역 리스트 조회")
    fun receipt(): ResponseEntity<List<ReceiptsInfoTO>> {
        val userId = userContextUtil.userId ?: throw IllegalStateException("사용자 ID가 필요합니다")
        return ResponseEntity.ok(paymentService.getReceiptsInfoByCustomerId(userId))
    }

    @GetMapping("/reservation/{landId}")
    @Operation(summary = "임대지 예약 리스트 조회")
    fun reservation(@PathVariable landId: Long): ResponseEntity<ReservationResponse> {
        val landTitle = rentalLandService.findById(landId).title
        val reservations = paymentService.getReceiptsInfoByLandId(landId)

        return ResponseEntity.ok(
            ReservationResponse(
                landTitle = landTitle.toString(),
                reservation = reservations
            )
        )
    }

    @PatchMapping("/receipt/{orderId}")
    @Operation(summary = "임대지 결제 환불")
    fun receipt(@PathVariable orderId: String): ResponseEntity<Void> {
        paymentService.changeReservationStatus(orderId)
        return ResponseEntity.ok().build()
    }
}
