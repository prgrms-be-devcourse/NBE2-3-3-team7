package com.project.popupmarket.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TossPaymentTO {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
