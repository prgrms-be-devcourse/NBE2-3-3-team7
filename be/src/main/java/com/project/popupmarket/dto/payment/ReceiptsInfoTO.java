package com.project.popupmarket.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ReceiptsInfoTO {
    private String orderId;
    private String status;
    private String landTitle;
    private String customerName;
    private BigDecimal price;
    private BigDecimal amount;
    private LocalDate start;
    private LocalDate end;
}
