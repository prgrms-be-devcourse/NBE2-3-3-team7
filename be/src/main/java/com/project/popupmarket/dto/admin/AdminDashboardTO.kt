package com.project.popupmarket.dto.admin

import com.project.popupmarket.dto.payment.ReceiptsManageTO

data class AdminDashboardTO(
    val receipts: ReceiptsManageTO,
    val customer: String,
    val landlord: String,
)
