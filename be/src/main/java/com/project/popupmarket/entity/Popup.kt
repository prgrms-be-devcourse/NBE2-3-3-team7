package com.project.popupmarket.entity

import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.AgeGroup
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "popup")
class Popup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "customer_id", nullable = false)
    var customerId: Long? = null,

    @Column(nullable = false)
    var type: String? = null,

    @Column(nullable = false)
    var title: String? = null,

    @Column(name = "zipcode", length = 5, nullable = false)
    var zipcode: String? = null,

    @Column(name = "address", nullable = false)
    var address: String? = null,

    @Column(name = "addr_detail", nullable = false)
    var addrDetail: String? = null,

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String? = null,

    @Column(name = "infra", nullable = false)
    var infra: String? = null,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate? = null,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    var ageGroup: AgeGroup? = null,

    @Column(name = "registered_at", nullable = false, updatable = false)
    var registeredAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ActivateStatus = ActivateStatus.ACTIVE,
)
{
    // 기본 생성자
    constructor() : this(
        id = 0,
        customerId = 0,
        type = "",
        title = "",
        zipcode = "",
        address = "",
        addrDetail = "",
        description = "",
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusDays(1),
        ageGroup = null,
        registeredAt = LocalDateTime.now(),
        status = ActivateStatus.ACTIVE
    )
}