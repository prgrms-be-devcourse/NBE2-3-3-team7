package com.project.popupmarket.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationResponse {
    private String landTitle;
    private List<ReceiptsInfoTO> reservation;
}
