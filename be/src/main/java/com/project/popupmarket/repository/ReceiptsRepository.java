package com.project.popupmarket.repository;

import com.project.popupmarket.entity.Receipts;
import com.project.popupmarket.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptsRepository extends JpaRepository<Receipts, String>, ReceiptsQueryDslRepository {
    @Query(
            "SELECT r FROM Receipts r " +
                    "WHERE (:reservation_status IS NULL OR r.reservationStatus = :reservation_status)"
    )
    Page<Receipts> findReceiptsByFilter(
            @Param("reservation_status") ReservationStatus reservation_status,
            @Param("sorting") String sorting,
            Pageable pageable
    );
}
