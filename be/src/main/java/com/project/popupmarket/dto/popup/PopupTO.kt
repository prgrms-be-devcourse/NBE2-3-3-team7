package com.project.popupmarket.dto.popup

import java.time.LocalDate
import java.time.LocalDateTime

data class PopupTO(
    var id: Long? = null,
    var customerId: Long? = null,
    var type: String? = null,
    var zipcode: String? = null,
    var address: String? = null,
    var addrDetail: String? = null,
    var title: String? = null,
    var description: String? = null,
    var infra: String? = null,
    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,
    var ageGroup: String? = null,
    var registeredAt: LocalDateTime? = null,
    var status: String? = null
)
