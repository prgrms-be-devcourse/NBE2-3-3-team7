package com.project.popupmarket.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StagingPayment implements Serializable {
    private String orderId;
    private Long customerId;
    private Long rentalLandId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
}
