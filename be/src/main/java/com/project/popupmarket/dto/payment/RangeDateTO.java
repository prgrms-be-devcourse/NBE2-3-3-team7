package com.project.popupmarket.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RangeDateTO {
    private LocalDate start;
    private LocalDate end;
}
