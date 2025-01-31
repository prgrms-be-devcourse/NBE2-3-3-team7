package com.project.popupmarket.repository

import com.project.popupmarket.entity.BusinessId
import org.springframework.data.jpa.repository.JpaRepository

interface BusinessIdRepository : JpaRepository<BusinessId, Long>