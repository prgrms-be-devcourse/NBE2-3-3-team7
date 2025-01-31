package com.project.popupmarket.repository

import com.project.popupmarket.entity.Receipts
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface ReceiptsRepository : JpaRepository<Receipts?, String?>, ReceiptsJDslRepository{
    @Query(
        """SELECT r, customerUser.name, landlordUser.name FROM Receipts r 
        JOIN User customerUser ON r.customerId = customerUser.id 
        JOIN User landlordUser ON r.rentalLandId = landlordUser.id 
        WHERE r.reservedAt BETWEEN :startDate AND :endDate"""
    )
    fun findDashboardsBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Array<Any>>

    @Query("""
    SELECT COALESCE(SUM(r.amount), 0) 
    FROM Receipts r 
    WHERE r.reservedAt BETWEEN :startDate AND :endDate
    """)
    fun findTotalAmountByBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long

    @Query("""
    SELECT COUNT(r) 
    FROM Receipts r 
    WHERE r.reservedAt BETWEEN :startDate AND :endDate
    """)
    fun findTotalCountByBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long


}
