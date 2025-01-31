package com.project.popupmarket.repository

import com.project.popupmarket.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE u.id = :id")
    fun findUserInfoById(@Param("id") id: Long): User
}