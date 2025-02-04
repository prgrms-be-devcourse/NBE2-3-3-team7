package com.project.popupmarket.dto.admin

data class AdminPeriodAnalyticsTO(
    val current: List<Map<String, Any>> = emptyList(),
    val prev: List<Map<String, Any>> = emptyList(),
    val data: AdminDataAnalyticsTO = AdminDataAnalyticsTO()
)
