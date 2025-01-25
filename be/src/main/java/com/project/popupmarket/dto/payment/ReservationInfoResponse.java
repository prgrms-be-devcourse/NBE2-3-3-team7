package com.project.popupmarket.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ReservationInfoResponse {
    private String customerKey;
    private String customerName;
    private String customerEmail;
    private String customerTel;
    private String landTitle;
    private String zipcode;
    private String address;
    private String addrDetail;
    private BigDecimal price;
}
