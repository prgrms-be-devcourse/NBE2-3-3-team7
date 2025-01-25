package com.project.popupmarket.scheduler;

import com.project.popupmarket.repository.ReceiptsQueryDslRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReceiptsScheduler {

    private final ReceiptsQueryDslRepositoryImpl receiptsQueryDslRepositoryImpl;
    @PersistenceContext
    private EntityManager em;

    public ReceiptsScheduler(ReceiptsQueryDslRepositoryImpl receiptsQueryDslRepositoryImpl) {
        this.receiptsQueryDslRepositoryImpl = receiptsQueryDslRepositoryImpl;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void rentalPlaceDailyChangeStatus() {
        long affected = receiptsQueryDslRepositoryImpl.dailyUpdateReservationStatusToLeased();
        log.info("Receipt 예약 상태 변경 : {}", affected);
    }
}
