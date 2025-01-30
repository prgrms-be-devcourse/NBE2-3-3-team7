package com.project.popupmarket.repository

import com.project.popupmarket.entity.RentalLand
import com.project.popupmarket.enums.ActivateStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface RentalLandJpaRepository : JpaRepository<RentalLand, Long> {
    //jpql
    @Query(value = "SELECT rp FROM RentalLand rp ORDER BY rp.registeredAt DESC LIMIT 10")
    fun findByLimit(): List<RentalLand>

    @Query(
        ("SELECT rp " +
                "FROM RentalLand rp " +
                "WHERE rp.landlordId = :userId " +
                "ORDER BY rp.registeredAt DESC")
    )
    fun findByUserId(@Param("userId") userId: Long): List<RentalLand>

    @Query(
        ("SELECT rp " +
                "FROM RentalLand rp " +
                "WHERE rp.landlordId = :userId " +
                "AND rp.status = 'active'")
    )
    fun findActivatedRentalPlacesByUserId(
        @Param("userId") userId: Long,
        @Param("popupId") popupId: Long
    ): List<RentalLand>


    @Query(
        value = ("SELECT rp " +
                "FROM RentalLand rp " +
                "WHERE rp.status = 'ACTIVE' " +
                "AND rp.area BETWEEN :minArea AND :maxArea " +
                "AND (:location IS NULL OR rp.address LIKE %:location%) " +
                "AND rp.price BETWEEN :minPrice AND :maxPrice " +
                "AND NOT EXISTS (" +
                "   SELECT 1 FROM Receipts r " +
                "   WHERE r.rentalLandId = rp.id " +
                "   AND (r.startDate <= :endDate AND r.endDate >= :startDate) " +
                "   AND r.reservationStatus != 'CANCELED'" +
                ")" +
                "ORDER BY " +
                "CASE WHEN :sorting = 'registered_desc' THEN rp.registeredAt END DESC, " +
                "CASE WHEN :sorting = 'registered_asc' THEN rp.registeredAt END ASC, " +
                "CASE WHEN :sorting = 'area_desc' THEN rp.area END DESC, " +
                "CASE WHEN :sorting = 'area_asc' THEN rp.area END ASC, " +
                "CASE WHEN :sorting = 'price_desc' THEN rp.price END DESC, " +
                "CASE WHEN :sorting = 'price_asc' THEN rp.price END ASC, " +
                "CASE WHEN :sorting IS NULL OR :sorting = '' THEN rp.registeredAt END DESC," +
                "CASE WHEN :sorting NOT IN ('registered_desc', 'registered_asc') THEN rp.registeredAt END DESC")
    )
    fun findByFilter(
        @Param("minArea") minArea: Int?,
        @Param("maxArea") maxArea: Int?,
        @Param("location") location: String?,
        @Param("minPrice") minPrice: BigDecimal?,
        @Param("maxPrice") maxPrice: BigDecimal?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?,
        @Param("sorting") sorting: String?,
        pageable: Pageable?
    ): Page<RentalLand>

    @Query(
        ("SELECT r FROM RentalLand r "+
                "WHERE (:address IS NULL OR r.address LIKE %:address%) "+
                "AND (:title IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
                "AND (:status IS NULL OR r.status = :status)")
    )
    fun findLandAdminByFilter(
        @Param("address") address: String?,
        @Param("status") status: ActivateStatus?,
        @Param("title") title: String?,
        @Param("sorting") sorting: String?,
        pageable: Pageable?
    ): Page<RentalLand>

    @Modifying
    @Query("DELETE FROM RentalLand r WHERE r.id = :id")
    fun deleteRentalLandById(@Param("id") id: Long)

    @Modifying
    @Query("UPDATE RentalLand r SET r.status = :status WHERE r.id = :id")
    fun updateStatusById(@Param("id") id: Long, @Param("status") status: String)
}