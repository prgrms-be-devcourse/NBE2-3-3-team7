package com.project.popupmarket.dto.admin;


import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMonthlyCountTO {
    private Long monthlyCount;
    private Long monthly;
    private Long weekly;
    private Long daily;
}

