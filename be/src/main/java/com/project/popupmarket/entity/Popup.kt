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
    val id: Long = 0,

    @Column(name = "customer_id", nullable = false)
    var customerId: Long = 0,

    @Column(nullable = false)
    var type: String = "",

    @Column(nullable = false)
    var title: String = "",

    @Column(name = "zipcode", length = 5, nullable = false)
    var zipcode: String = "",

    @Column(name = "address", nullable = false)
    var address: String = "",

    @Column(name = "addr_detail", nullable = false)
    var addrDetail: String = "",

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String = "",

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate = LocalDate.now(),

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate = LocalDate.now().plusDays(1),

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    var ageGroup: AgeGroup = AgeGroup.TWENTIES,

    @Column(name = "registered_at", nullable = false, updatable = false)
    var registeredAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ActivateStatus = ActivateStatus.ACTIVE
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
        ageGroup = AgeGroup.TWENTIES,
        registeredAt = LocalDateTime.now(),
        status = ActivateStatus.ACTIVE
    )
}