package com.project.popupmarket.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.popupmarket.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptsManageTO {
    private String paymentKey;
    private BigDecimal amount;
    private LocalDate start;
    private LocalDate end;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
}
