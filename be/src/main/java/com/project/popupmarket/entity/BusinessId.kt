package com.project.popupmarket.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "business_info")
class BusinessId(
    @Id
    val userId: Long = 0,

    @Column(nullable = false)
    val businessId: String
) {
    constructor() : this(
        userId = 0,
        businessId = ""
    ) {
    }
}