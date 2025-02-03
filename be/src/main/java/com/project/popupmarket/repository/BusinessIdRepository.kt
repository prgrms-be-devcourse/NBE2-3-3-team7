package com.project.popupmarket.repository

import com.project.popupmarket.entity.BusinessId
import com.project.popupmarket.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BusinessIdRepository : JpaRepository<BusinessId, Long> {
    @Query("SELECT b.businessId FROM BusinessId b WHERE b.businessId = :businessId")
    fun findByBusinessId(@Param("businessId") businessId: String): String?
}