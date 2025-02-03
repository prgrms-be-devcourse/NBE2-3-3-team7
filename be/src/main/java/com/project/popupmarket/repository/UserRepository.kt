package com.project.popupmarket.repository

import com.project.popupmarket.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE u.id = :id")
    fun findUserInfoById(@Param("id") id: Long): User

    @Query(
        """
        SELECT u.role, COUNT(u) 
        FROM User u 
        GROUP BY u.role
    """
    )
    fun countUsersByRole(): List<Array<Any>>

    @Query(
        """
        SELECT FUNCTION('DATE_FORMAT', u.registeredAt, '%Y-%m-%d'), COUNT(u)
        FROM User u 
        WHERE u.registeredAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('DATE_FORMAT', u.registeredAt, '%Y-%m-%d')
        """
    )
    fun findUsersByRegisteredAtBetween(
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?,
    ): List<Array<Any>>
}