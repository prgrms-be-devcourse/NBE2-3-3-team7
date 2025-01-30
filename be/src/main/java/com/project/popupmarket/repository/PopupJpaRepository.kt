package com.project.popupmarket.repository

import com.project.popupmarket.entity.Popup
import com.project.popupmarket.entity.RentalLand
import com.project.popupmarket.enums.ActivateStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

@Repository
interface PopupJpaRepository : JpaRepository<Popup, Long> {
    //jpql
    @Query(value = "SELECT po FROM Popup po ORDER BY po.registeredAt DESC LIMIT 10")
    fun findWithLimit(): List<Popup>

    @Query(
        ("SELECT po " +
                "FROM Popup po " +
                "WHERE po.customerId = :userId " +
                "ORDER BY po.registeredAt DESC")
    )
    fun findPopupByUserId(@Param("userId") userId: Long): List<Popup>

    @Query(
        ("SELECT po " +
                "FROM Popup po " +
                "WHERE po.customerId = :userId " +
                "AND po.status = 'active'")
    )
    fun findActivatedPopupByUserId(
        @Param("userId") userId: Long,
        @Param("popupId") popupId: Long
    ): List<Popup>

    @Query("""
    SELECT po 
    FROM Popup po 
    WHERE po.status = 'ACTIVE' 
    AND (:targetLocation IS NULL OR po.address LIKE CONCAT('%', :targetLocation, '%')) 
    AND (:type IS NULL OR po.type LIKE CONCAT('%', :type, '%')) 
    AND (:targetAgeGroup IS NULL OR po.ageGroup = :targetAgeGroup) 
    AND po.startDate BETWEEN :startDate AND :endDate 
    AND po.endDate BETWEEN :startDate AND :endDate 
    ORDER BY 
    CASE 
        WHEN :sorting = 'registered_desc' THEN po.registeredAt 
        WHEN :sorting = 'registered_asc' THEN po.registeredAt 
        WHEN :sorting = 'area_desc' THEN po.zipcode 
        WHEN :sorting = 'area_asc' THEN po.zipcode 
        ELSE po.registeredAt 
    END DESC
""")
    fun findFilteredWithPagination(
        @Param("targetLocation") targetLocation: String?,
        @Param("type") type: String?,
        @Param("targetAgeGroup") targetAgeGroup: String?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?,
        @Param("sorting") sorting: String?,
        pageable: Pageable?
    ): Page<Popup>

    @Query(
        ("SELECT p FROM Popup p "+
                "WHERE (:address IS NULL OR p.address LIKE CONCAT('%', :address, '%')) " +
                "AND (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
                "AND (:type IS NULL OR LOWER(p.type) LIKE LOWER(CONCAT('%', :type, '%'))) " +
                "AND (:status IS NULL OR p.status = :status)")
    )
    fun findPopupAdminByFilter(
        @Param("address") address: String?,
        @Param("status") status: ActivateStatus?,
        @Param("title") title: String?,
        @Param("type") type: String?,
        @Param("sorting") sorting: String?,
        pageable: Pageable?
    ): Page<Popup>

    @Query("SELECT p.customerId FROM Popup p WHERE p.id = :id")
    fun findUserSeqById(@Param("id") id: Long): Long

    @Modifying
    @Query("DELETE FROM Popup p WHERE p.id = :id")
    fun deletePopupById(@Param("id") id: Long)

    @Modifying
    @Query("UPDATE Popup p SET p.status = :status WHERE p.id = :id")
    fun updateStatusById(@Param("id") id: Long, @Param("status") status: String)
}