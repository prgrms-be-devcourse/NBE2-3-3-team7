package com.project.popupmarket.dto.admin;

import com.project.popupmarket.enums.ActivateStatus;
import com.project.popupmarket.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryTO {
    private List<AdminDashboardTO> dashboardList;  // 개별 거래 내역 리스트
    private List<Object[]>  dailyCounts;  // 일일 거래량 요약 데이터
    private List<Object[]> weeklyRegistered;
    private Map<Role, Long> countUsersByRole;
    private Map<ActivateStatus, Long> totalRentalLands;
    private Map<ActivateStatus, Long> totalPopups;
    private AdminMonthlyCountTO monthlyCount;
}

