
package com.project.popupmarket.repository;

import com.project.popupmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findUserInfoById(@Param("id") Long id);

    @Query("""
    SELECT u.role, COUNT(u) 
    FROM User u 
    GROUP BY u.role
    """)
    List<Object[]> countUsersByRole();

    @Query("""
        SELECT FUNCTION('DATE_FORMAT', u.registeredAt, '%Y-%m-%d'), COUNT(u)
        FROM User u 
        WHERE u.registeredAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('DATE_FORMAT', u.registeredAt, '%Y-%m-%d')"""
    )
    List<Object[]> findUsersByRegisteredAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
