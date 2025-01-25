package com.project.popupmarket.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptsTO {
    private String paymentKey;
    private String orderId;
    private Long customerId;
    private Long landId;
    private LocalDate start;
    private LocalDate end;
    private BigDecimal amount;

    public ReceiptsTO withCustomerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }
}
