package com.project.popupmarket.repository;

import com.project.popupmarket.dto.payment.RangeDateTO;
import com.project.popupmarket.dto.payment.ReceiptsInfoTO;
import com.project.popupmarket.dto.payment.ReservationTO;
import com.project.popupmarket.entity.Receipts;

import java.util.List;

public interface ReceiptsQueryDslRepository {
    boolean reservationDateCheck(ReservationTO reservation);
    List<ReceiptsInfoTO> getReceiptsByLandId(Long landId);
    List<ReceiptsInfoTO> getReceiptsByCustomerId(Long customerId);
    Receipts findReceiptsByOrderId(String orderId);
    List<RangeDateTO> getRangeDates(Long landId);
    long updateReservationStatusToCanceledByOrderId(String orderId);
    long dailyUpdateReservationStatusToLeased();
}
