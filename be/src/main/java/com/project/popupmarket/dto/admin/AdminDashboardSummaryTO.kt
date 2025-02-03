package com.project.popupmarket.dto.admin

import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.Role

data class AdminDashboardSummaryTO(
    val dashboardList: List<AdminDashboardTO> = emptyList(),  // 개별 거래 내역 리스트
    val dailyCounts: List<Map<String, Any>> = emptyList(),  // 일일 거래량 요약 데이터
    val weeklyRegistered: List<Map<String, Any>> = emptyList(),
    val countUsers: Map<Role, Long> = emptyMap(),
    val totalLands: Map<ActivateStatus, Long> = emptyMap(),
    val totalPopups: Map<ActivateStatus, Long> = emptyMap(),
    val monthlyCount: AdminMonthlyCountTO = AdminMonthlyCountTO()
)

