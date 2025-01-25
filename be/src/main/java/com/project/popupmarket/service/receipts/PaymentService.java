package com.project.popupmarket.service.receipts;

import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.entity.*;
import com.project.popupmarket.enums.ReservationStatus;
import com.project.popupmarket.exception.custom.PaymentException;
import com.project.popupmarket.repository.ReceiptsQueryDslRepositoryImpl;
import com.project.popupmarket.repository.ReceiptsRepository;
import com.project.popupmarket.util.UserContextUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final StagingPaymentRedisService stagingPaymentRedisService;
    private final ReceiptsRepository receiptsRepository;
    private final TossRequestService tossRequestService;
    private final UserContextUtil userContextUtil;
    private final JPAQueryFactory queryFactory;
    private final ReceiptsQueryDslRepositoryImpl receiptsQueryDslRepositoryImpl;

    @Transactional
    public void paymentProcess(TossPaymentTO payment) {
        ReceiptsTO receipt = tossRequestService.requestPayment(payment)
                .withCustomerId(userContextUtil.getUserId());

        try {
            insertReceipt(receipt);
        } catch (Exception e) {
            tossRequestService.cancelPayment(payment.getPaymentKey(), "시스템 에러로 인한 결제 취소");
            log.error("영수증 저장 실패로 결제 취소 처리: {}", e.getMessage());
            throw new PaymentException("결제 실패: 영수증 저장 중 오류 발생", e);
        }
    }

    public ReservationInfoResponse getPaymentInfo(ReservationTO reservation) {
        boolean isReservationValid = receiptsRepository.reservationDateCheck(reservation);

        if (!isReservationValid) {
            throw new IllegalArgumentException("이미 예약된 날짜입니다.");
        }

        QUser qUser = QUser.user;
        QRentalLand qRentalLand = QRentalLand.rentalLand;

        User user = queryFactory.selectFrom(qUser)
                .where(qUser.id.eq(reservation.getCustomerId()))
                .fetchOne();

        RentalLand rentalLand = queryFactory.selectFrom(qRentalLand)
                .where(qRentalLand.id.eq(reservation.getLandId()))
                .fetchOne();

        if (user == null || rentalLand == null) {
            throw new IllegalArgumentException("사용자 또는 임대지를 찾을 수 없습니다.");
        }

        return ReservationInfoResponse.builder()
                .customerKey(UUID.randomUUID().toString())
                .landTitle(rentalLand.getTitle())
                .price(rentalLand.getPrice())
                .customerEmail(user.getEmail())
                .customerName(user.getName())
                .customerTel(user.getTel())
                .zipcode(rentalLand.getZipcode())
                .address(rentalLand.getAddress())
                .addrDetail(rentalLand.getAddrDetail())
                .build();
    }

    @Transactional
    public void insertStagingPayment(ReceiptsTO receipt) {
        StagingPayment stagingPayment = StagingPayment.builder()
                .orderId(receipt.getOrderId())
                .customerId(receipt.getCustomerId())
                .rentalLandId(receipt.getLandId())
                .startDate(receipt.getStart())
                .endDate(receipt.getEnd())
                .totalAmount(receipt.getAmount())
                .build();

        stagingPaymentRedisService.save(stagingPayment.getOrderId(), stagingPayment);

        StagingPayment saved = stagingPaymentRedisService.find(stagingPayment.getOrderId());
        if (saved == null || !saved.getOrderId().equals(stagingPayment.getOrderId())) {
            log.error("Redis 저장 실패: Order ID = {}", stagingPayment.getOrderId());
            throw new PaymentException("Redis에 결제 데이터를 저장하는 데 실패하였습니다.");
        }
    }

    @Transactional
    public void insertReceipt(ReceiptsTO receipt) {
        StagingPayment stagingPayment = stagingPaymentRedisService.find(receipt.getOrderId());

        if (stagingPayment == null) {
            throw new IllegalArgumentException("해당 Order ID에 대한 StagingPayment를 찾을 수 없습니다.");
        }

        Receipts receipts = Receipts.builder()
                .paymentKey(receipt.getPaymentKey())
                .orderId(receipt.getOrderId())
                .customerId(receipt.getCustomerId())
                .rentalLandId(receipt.getLandId())
                .startDate(receipt.getStart())
                .endDate(receipt.getEnd())
                .amount(receipt.getAmount())
                .reservationStatus(ReservationStatus.COMPLETED)
                .build();

        receiptsRepository.save(receipts);
        stagingPaymentRedisService.delete(receipt.getOrderId());
    }

    @Transactional
    public void deleteStagingPayment(ReceiptsTO receipt) {
        try {
            StagingPayment stagingPayment = stagingPaymentRedisService.find(receipt.getOrderId());

            if (stagingPayment == null) {
                throw new IllegalArgumentException("주문 번호를 찾을 수 없습니다. " + receipt.getOrderId());
            }

            stagingPaymentRedisService.delete(receipt.getOrderId());
        } catch (IllegalArgumentException e) {
            log.warn("해당 임시 결제 내역을 삭제할 수 없습니다. : {}", e.getMessage());
            throw e; // 필요한 경우 다시 던짐
        } catch (Exception e) {
            log.error("임시 결제 내역을 삭제하면서 알 수 없는 오류가 발생했습니다.");
            throw new PaymentException("임시 결제 내역 삭제에 실패했습니다.");
        }
    }

    public List<ReceiptsInfoTO> getReceiptsInfoByLandId(Long landId) {
        return receiptsQueryDslRepositoryImpl.getReceiptsByLandId(landId);
    }

    public List<ReceiptsInfoTO> getReceiptsInfoByCustomerId(Long customerId) {
        return receiptsQueryDslRepositoryImpl.getReceiptsByCustomerId(customerId);
    }

    @Transactional
    public void changeReservationStatus(String orderId) {
        Receipts receipts = receiptsQueryDslRepositoryImpl.findReceiptsByOrderId(orderId);

        if (receipts == null) {
            throw new IllegalArgumentException("해당 주문 번호에 대한 예약 정보를 찾을 수 없습니다.");
        }

        long affected = receiptsRepository.updateReservationStatusToCanceledByOrderId(orderId);

        if (affected == 0) {
            throw new PaymentException("");
        }

        TossPaymentTO payment = TossPaymentTO.builder()
                .paymentKey(receipts.getPaymentKey())
                .orderId(receipts.getOrderId())
                .amount(receipts.getAmount())
                .build();

        if (payment == null) {
            throw new PaymentException("");
        }

        tossRequestService.cancelPayment(payment.getPaymentKey(), "시스템 에러로 인한 결제 취소");
    }

    public List<RangeDateTO> getRangeDates(Long rentalPlaceSeq) {
        return receiptsQueryDslRepositoryImpl.getRangeDates(rentalPlaceSeq);
    }
}