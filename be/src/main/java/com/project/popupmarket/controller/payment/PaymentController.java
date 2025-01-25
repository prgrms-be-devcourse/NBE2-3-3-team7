package com.project.popupmarket.controller.payment;

import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.service.receipts.PaymentService;
import com.project.popupmarket.service.land.RentalLandService;
import com.project.popupmarket.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserContextUtil userContextUtil;
    private final RentalLandService rentalLandService;

    @Autowired
    public PaymentController(PaymentService paymentService, UserContextUtil userContextUtil, RentalLandService rentalLandService) {
        this.paymentService = paymentService;
        this.userContextUtil = userContextUtil;
        this.rentalLandService = rentalLandService;
    }

    @GetMapping("/payment")
    @Operation(summary = "임대지 및 예약자 정보 조회")
    public ResponseEntity<ReservationInfoResponse> reservationInfo(
            @RequestParam Long landId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        ReservationTO reservation = ReservationTO.builder()
                .customerId(userContextUtil.getUserId())
                .landId(landId)
                .start(start)
                .end(end)
                .build();

        ReservationInfoResponse resp = paymentService.getPaymentInfo(reservation);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/payment")
    @Operation(summary = "임시 결제 내역 추가")
    public ResponseEntity<Void> payment(@RequestBody ReceiptsTO receipt) {
        Long customerId = userContextUtil.getUserId();

        paymentService.insertStagingPayment(receipt.withCustomerId(customerId));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment/success")
    @Operation(summary = "결제 승인 요청 및 영수증 추가")
    public ResponseEntity<Void> paymentSuccess(@RequestBody TossPaymentTO payment) {
        paymentService.paymentProcess(payment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/payment/fail")
    @Operation(summary = "결제 실패, 임시 결제 내역 삭제")
    public ResponseEntity<String> paymentFail(@RequestBody ReceiptsTO receipt) {
        paymentService.deleteStagingPayment(receipt);

        return ResponseEntity.ok("success");
    }

    @GetMapping("/receipt")
    @Operation(summary = "사용자 결제 내역 리스트 조회")
    public ResponseEntity<List<ReceiptsInfoTO>> receipt() {
        Long userId = userContextUtil.getUserId();

        return ResponseEntity.ok(paymentService.getReceiptsInfoByCustomerId(userId));
    }

    @GetMapping("/reservation/{landId}")
    @Operation(summary = "임대지 예약 리스트 조회")
    public ResponseEntity<ReservationResponse> reservation(@PathVariable Long landId) {

        return ResponseEntity.ok(
                ReservationResponse.builder()
                        .landTitle(rentalLandService.findById(landId).getTitle())
                        .reservation(paymentService.getReceiptsInfoByLandId(landId))
                        .build()
        );
    }

    @PatchMapping("/receipt/{orderId}")
    @Operation(summary = "임대지 결제 환불")
    public ResponseEntity<Void> receipt(@PathVariable String orderId) {
        paymentService.changeReservationStatus(orderId);

        return ResponseEntity.ok().build();
    }
}
