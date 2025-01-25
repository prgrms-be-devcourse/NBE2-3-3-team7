package com.project.popupmarket.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReservationTO {
    private Long customerId;
    private Long landId;
    private LocalDate start;
    private LocalDate end;
}
