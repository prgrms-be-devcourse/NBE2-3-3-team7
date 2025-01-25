package com.project.popupmarket.repository;

import com.project.popupmarket.dto.payment.RangeDateTO;
import com.project.popupmarket.dto.payment.ReceiptsInfoTO;
import com.project.popupmarket.dto.payment.ReservationTO;
import com.project.popupmarket.entity.*;
import com.project.popupmarket.enums.ReservationStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReceiptsQueryDslRepositoryImpl implements ReceiptsQueryDslRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean reservationDateCheck(ReservationTO reservation) {
        QReceipts qReceipt = QReceipts.receipts;

        return query.select(qReceipt)
                .from(qReceipt)
                .where(qReceipt.rentalLandId.eq(reservation.getLandId())
                        .and(qReceipt.startDate.loe(reservation.getEnd()).and(qReceipt.endDate.goe(reservation.getStart())))
                        .and(qReceipt.reservationStatus.ne(ReservationStatus.CANCELED))
                ).fetch().isEmpty();
    }

    @Override
    public List<ReceiptsInfoTO> getReceiptsByLandId(Long landId) {
        QReceipts qReceipts = QReceipts.receipts;
        QUser qUser = QUser.user;

        List<ReceiptsInfoTO> receiptsInfo = new ArrayList<>();

        query.select(qReceipts)
                .from(qReceipts)
                .where(qReceipts.rentalLandId.eq(landId).and(
                        qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED)
                ))
                .orderBy(qReceipts.reservedAt.desc())
                .fetch()
                .forEach(receipt -> {
                    ReceiptsInfoTO receiptsInfoTO = ReceiptsInfoTO.builder()
                            .orderId(receipt.getOrderId())
                            .status(receipt.getReservationStatus().getDesc())
                            .customerName(query.select(qUser.name).from(qUser).where(qUser.id.eq(receipt.getCustomerId())).fetchOne())
                            .amount(receipt.getAmount())
                            .price(receipt.getAmount().divide(BigDecimal.valueOf(
                                    ChronoUnit.DAYS.between(receipt.getStartDate(), receipt.getEndDate()) + 1
                            ), 0, RoundingMode.UP))
                            .start(receipt.getStartDate())
                            .end(receipt.getEndDate())
                            .build();
                    receiptsInfo.add(receiptsInfoTO);
                });

        return receiptsInfo;
    }

    @Override
    public List<ReceiptsInfoTO> getReceiptsByCustomerId(Long customerId) {
        QReceipts qReceipts = QReceipts.receipts;
        QRentalLand qRentalLand = QRentalLand.rentalLand;

        List<ReceiptsInfoTO> receiptInfoList = new ArrayList<>();

        query.select(qReceipts)
                .from(qReceipts)
                .where(qReceipts.customerId.eq(customerId))
                .orderBy(qReceipts.reservedAt.desc())
                .fetch()
                .forEach(receipt -> {
                    ReceiptsInfoTO receiptsInfoTO = ReceiptsInfoTO.builder()
                            .orderId(receipt.getOrderId())
                            .status(receipt.getReservationStatus().getDesc())
                            .landTitle(query.select(qRentalLand.title).from(qRentalLand).where(qRentalLand.id.eq(receipt.getRentalLandId())).fetchOne())
                            .amount(receipt.getAmount())
                            .price(receipt.getAmount().divide(BigDecimal.valueOf(
                                    ChronoUnit.DAYS.between(receipt.getStartDate(), receipt.getEndDate()) + 1
                            ), 0, RoundingMode.UP))
                            .start(receipt.getStartDate())
                            .end(receipt.getEndDate())
                            .build();
                    receiptInfoList.add(receiptsInfoTO);
                });

        return receiptInfoList;
    }

    @Override
    public Receipts findReceiptsByOrderId(String orderId) {
        QReceipts qReceipts = QReceipts.receipts;

        return query.select(qReceipts)
                .from(qReceipts)
                .where(qReceipts.orderId.eq(orderId))
                .fetchOne();
    }

    @Override
    public List<RangeDateTO> getRangeDates(Long landId) {
        QReceipts qReceipts = QReceipts.receipts;

        return query.select(Projections.constructor(RangeDateTO.class, qReceipts.startDate, qReceipts.endDate)).from(qReceipts)
                .where(qReceipts.rentalLandId.eq(landId)
                        .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED))
                        .and(qReceipts.startDate.goe(LocalDate.now())))
                .fetch();
    }

    @Override
    public long updateReservationStatusToCanceledByOrderId(String orderId) {
        QReceipts qReceipts = QReceipts.receipts;

        return query.update(qReceipts)
                .set(qReceipts.reservationStatus, ReservationStatus.CANCELED)
                .where(qReceipts.orderId.eq(orderId))
                .execute();
    }

    @Override
    public long dailyUpdateReservationStatusToLeased() {
        QReceipts qReceipts = QReceipts.receipts;

        LocalDate today = LocalDate.now();

        return query.update(qReceipts)
                .set(qReceipts.reservationStatus, ReservationStatus.LEASED)
                .where(qReceipts.startDate.goe(today)
                        .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED)))
                .execute();
    }
}
