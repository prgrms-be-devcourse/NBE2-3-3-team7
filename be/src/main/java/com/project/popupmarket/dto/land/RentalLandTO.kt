package com.project.popupmarket.dto.land

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RentalLandTO(
    var id: Long? = null,
    var landlordId: Long? = null,
    var price: BigDecimal? = null,
    var zipcode: String? = null,
    var address: String? = null,
    var addrDetail: String? = null,
    var description: String? = null,
    var infra: String? = null,
    var title: String? = null,
    var area: Int? = null,
    var ageGroup: String? = null,
    var registeredAt: LocalDateTime? = null,
    var status: String? = null
)
