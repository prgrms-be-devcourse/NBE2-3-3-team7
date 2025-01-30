package com.project.popupmarket.scheduler

import com.project.popupmarket.repository.ReceiptsJDslRepositoryImpl
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
open class ReceiptsScheduler(
    private val receiptsQueryDslRepositoryImpl: ReceiptsJDslRepositoryImpl
) {

    private val log: Logger = LoggerFactory.getLogger(ReceiptsScheduler::class.java)

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    open fun rentalPlaceDailyChangeStatus() {
        val affected: Long = receiptsQueryDslRepositoryImpl.dailyUpdateReservationStatusToLeased()
        log.info("Receipt 예약 상태 변경 : {}", affected)
    }
}
