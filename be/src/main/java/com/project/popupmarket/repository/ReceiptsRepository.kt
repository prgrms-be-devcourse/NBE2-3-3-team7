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
        JOIN RentalLand land ON r.rentalLandId = land.id 
        JOIN User landlordUser ON land.landlordId = landlordUser.id
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

    @Query(
        """SELECT FUNCTION('DATE_FORMAT', r.reservedAt, '%m-%d'), SUM(r.amount)
    FROM Receipts r
    WHERE r.reservedAt BETWEEN :startDate AND :endDate
    GROUP BY FUNCTION('DATE_FORMAT', r.reservedAt, '%m-%d')
    ORDER BY FUNCTION('DATE_FORMAT', r.reservedAt, '%m-%d') ASC
    """)
    fun findDailySalesBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Array<Any>>

    @Query(
        """SELECT FUNCTION('DATE_FORMAT', r.reservedAt, '%m'), SUM(r.amount)
       FROM Receipts r
       WHERE r.reservedAt BETWEEN :startDate AND :endDate
       GROUP BY FUNCTION('DATE_FORMAT', r.reservedAt, '%m')
       ORDER BY FUNCTION('DATE_FORMAT', r.reservedAt, '%m') ASC
    """
    )
    fun findMonthlySalesBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Array<Any>>

    @Query(
        """SELECT rl.address, SUM(r.amount)
        FROM Receipts r
        JOIN RentalLand rl ON r.rentalLandId = rl.id
        WHERE r.reservedAt BETWEEN :startDate AND :endDate
        GROUP BY rl.address
        ORDER BY SUM(r.amount) DESC
    """)
    fun findRevenueByAddress(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Array<Any>>


}
