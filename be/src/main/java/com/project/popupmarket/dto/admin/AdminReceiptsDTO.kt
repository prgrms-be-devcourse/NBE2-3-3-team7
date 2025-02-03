package com.project.popupmarket.dto.admin

import com.project.popupmarket.dto.land.RentalLandTO
import com.project.popupmarket.dto.payment.ReceiptsManageTO
import com.project.popupmarket.dto.user.UserDto

data class AdminReceiptsDTO(
    val receiptsManageTO: ReceiptsManageTO,  // 거래/영수증 정보
    val rentalLand: RentalLandTO,  // 임대지 정보
    val userDto: UserDto  // 사용자 정보
)

