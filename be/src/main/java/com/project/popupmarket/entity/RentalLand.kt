package com.project.popupmarket.entity

import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.AgeGroup
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import lombok.Getter
import lombok.Setter
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "rental_land")
class RentalLand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "landlord_id", nullable = false)
    var landlordId: Long? = null

    @Column(name = "price", precision = 10, nullable = false)
    var price: BigDecimal? = null

    @Column(name = "zipcode", length = 5, nullable = false)
    var zipcode: @Size(min = 5, max = 5) String? = null

    @Column(name = "address", nullable = false)
    var address: @Size(max = 255) String? = null

    @Column(name = "addr_detail", nullable = false)
    var addrDetail: @Size(max = 255) String? = null

    @Column(name = "title", nullable = false)
    var title: @Size(max = 255) String? = null

    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    var description: String? = null

    @Column(name = "infra")
    var infra: @Size(max = 255) String? = null

    @Column(name = "area", nullable = false)
    var area: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    var ageGroup: AgeGroup? = null

    @Column(name = "registered_at", nullable = false, updatable = false)
    var registeredAt: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ActivateStatus? = ActivateStatus.ACTIVE
}