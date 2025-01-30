package com.project.popupmarket.repository

import com.project.popupmarket.entity.Receipts
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReceiptsRepository : JpaRepository<Receipts?, String?>, ReceiptsJDslRepository
