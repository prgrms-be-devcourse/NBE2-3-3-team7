package com.project.popupmarket.service.admin

import com.project.popupmarket.dto.admin.*
import com.project.popupmarket.dto.payment.ReceiptsManageTO
import com.project.popupmarket.dto.user.UserDto
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.ReservationStatus
import com.project.popupmarket.enums.Role
import com.project.popupmarket.repository.*
import com.project.popupmarket.service.land.RentalLandService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val rentalLandService: RentalLandService,
    private val receiptsRepository: ReceiptsRepository,
    private val rentalLandJpaRepository: RentalLandJpaRepository,
    private val popupJpaRepository: PopupJpaRepository,
    private val businessIdRepository: BusinessIdRepository,
) {

    fun findReceiptsAdminById(userId: Long): UserDto {
        val user = userRepository.findUserInfoById(userId)
        val businessId = businessIdRepository.findById(user.id!!).orElse(null)?.businessId

        return UserDto(
            id = user.id,
            email = user.email,
            name = user.name,
            brand = user.brand,
            role = user.role,
            tel = user.tel,
            businessId = businessId
        )
    }


    fun getAdminReceiptsInfo(reservationStatus: ReservationStatus?, sorting: String?, pageable: Pageable): Page<AdminReceiptsDTO> {
        return adminRepository.findReceiptsByFilter(reservationStatus, sorting, pageable)
            .map { receipt ->
                val receiptsManageTO = ReceiptsManageTO(
                    paymentKey = receipt.paymentKey,
                    amount = receipt.amount,
                    start = receipt.startDate,
                    end = receipt.endDate,
                    status = receipt.reservationStatus,
                    reservedAt = receipt.reservedAt
                )

                val user = findReceiptsAdminById(receipt.customerId)
                val rentalLandTO = rentalLandService.findById(receipt.rentalLandId)

                AdminReceiptsDTO(receiptsManageTO, rentalLandTO, user)
            }
    }

    /** 주간 통계 */
    fun getWeeklyDashboard(): AdminDashboardSummaryTO {
        val startDate = LocalDateTime.now().minusDays(6)
        val endDate = LocalDateTime.now()

        // 거래 내역 데이터 조회 및 변환
        val dashboardData = receiptsRepository.findDashboardsBetween(startDate, endDate)
        val dashboardList = dashboardData.map { obj ->
            AdminDashboardTO(
                receipts = ReceiptsManageTO(
                    paymentKey = (obj[0] as Receipts).paymentKey,
                    amount = (obj[0] as Receipts).amount,
                    start = (obj[0] as Receipts).startDate,
                    end = (obj[0] as Receipts).endDate,
                    status = (obj[0] as Receipts).reservationStatus,
                    reservedAt = (obj[0] as Receipts).reservedAt
                ),
                customer = obj[1] as String, // Popup 이름
                landlord = obj[2] as String  // RentalLand 이름
            )
        }

        // 일일 거래량 데이터
        val lastWeekDates = generateDateRange(startDate.toLocalDate(), endDate.toLocalDate(), "yyyy-MM-dd", ChronoUnit.DAYS)
        val dailyCounts = formatTransactionCounts(dashboardList, lastWeekDates)

        // 일일 가입자 수
        val weeklyRegistered  = userRepository.findUsersByRegisteredAtBetween(startDate, endDate)
        val formattedWeeklyRegistered = formatUserRegistrationCounts(weeklyRegistered, lastWeekDates)

        // 통계 데이터
        val countUsersByRoleMap :Map<Role, Long> = convertToMap(userRepository.countUsersByRole())
        val totalRentalLandsMap :Map<ActivateStatus, Long> = convertToMap(rentalLandJpaRepository.countRentalLandsByStatus())
        val totalPopupsMap :Map<ActivateStatus, Long> = convertToMap(popupJpaRepository.countPopupsByStatus())

        // 월/주/일 매출 통계
        val adminMonthlyCountTO = AdminMonthlyCountTO(
            monthlyCount = receiptsRepository.findTotalCountByBetween(startDate.minusMonths(1), endDate),
            monthly = receiptsRepository.findTotalAmountByBetween(startDate.minusMonths(1), endDate),
            weekly = receiptsRepository.findTotalAmountByBetween(startDate.minusWeeks(1), endDate),
            daily = receiptsRepository.findTotalAmountByBetween(startDate.minusDays(1), endDate)
        )

        return AdminDashboardSummaryTO(
            dashboardList = dashboardList,
            dailyCounts = dailyCounts,
            weeklyRegistered = formattedWeeklyRegistered,
            countUsers = countUsersByRoleMap,
            totalLands = totalRentalLandsMap,
            totalPopups = totalPopupsMap,
            monthlyCount = adminMonthlyCountTO
        )
    }


    /** 일간 통계 */
    fun getDailyAnalytics(days: Int): AdminPeriodAnalyticsTO {
        // 현재 주간 데이터 날짜 범위 설정
        val endDate = LocalDateTime.now()
        val startDate = endDate.minusDays(days.toLong())

        // 이전 주간 데이터 날짜 범위 설정
        val previousEndDate = startDate.minusDays(1)
        val previousStartDate = previousEndDate.minusDays(days.toLong())

        // 날짜 범위 생성 (MM-dd 형식)
        val dateRange = generateDateRange(startDate.toLocalDate(), endDate.toLocalDate(), "MM-dd", ChronoUnit.DAYS)

        // 데이터 맵 변환
        val currentSalesMap :Map<String, BigDecimal> = convertToMap(receiptsRepository.findDailySalesBetween(startDate, endDate))
        val previousSalesMap :Map<String, BigDecimal> = convertToMap(receiptsRepository.findDailySalesBetween(previousStartDate, previousEndDate))

        // 데이터 리스트 변환
        val formattedCurrentSales = formatSalesData(currentSalesMap, dateRange)
        val formattedPreviousSales = formatSalesData(previousSalesMap, dateRange)

        return AdminPeriodAnalyticsTO(
            current = formattedCurrentSales,
            prev = formattedPreviousSales,
            data = getRevenueByRegion(startDate, endDate)
        )
    }

    /** 월간 통계 */
    fun getMonthlyAnalytics(months: Int): AdminPeriodAnalyticsTO {
        val endMonth = LocalDateTime.now()
        val startMonth = endMonth.minusMonths(months.toLong())

        val previousEndMonth = startMonth.minusMonths(1)
        val previousStartMonth = previousEndMonth.minusMonths(months.toLong())

        // 날짜 범위 생성 (MM 형식)
        val monthsRange = generateDateRange(startMonth.toLocalDate(), endMonth.toLocalDate(), "MM", ChronoUnit.MONTHS)

        // 데이터 맵 변환
        val currentSalesMap :Map<String, BigDecimal> = convertToMap(receiptsRepository.findMonthlySalesBetween(startMonth, endMonth))
        val previousSalesMap :Map<String, BigDecimal> = convertToMap(receiptsRepository.findMonthlySalesBetween(previousStartMonth, previousEndMonth))

        // 데이터 리스트 변환
        val formattedCurrentSales = formatSalesData(currentSalesMap, monthsRange)
        val formattedPreviousSales = formatSalesData(previousSalesMap, monthsRange)

        return AdminPeriodAnalyticsTO(
            current = formattedCurrentSales,
            prev = formattedPreviousSales,
            data = getRevenueByRegion(startMonth, endMonth)
        )
    }

    companion object {
        private val REGION_MAPPING = mapOf(
            "서울" to "서울", "부산" to "부산", "대구" to "대구", "인천" to "인천",
            "광주" to "광주", "대전" to "대전", "울산" to "울산", "세종" to "세종",
            "경기" to "경기", "충북" to "충북", "충남" to "충남", "전북" to "전북",
            "전남" to "전남", "경북" to "경북", "경남" to "경남", "강원" to "강원",
            "제주" to "제주"
        )
    }

    /** 지역별 수익 */
    fun getRevenueByRegion(startDate: LocalDateTime, endDate: LocalDateTime): AdminDataAnalyticsTO {
        val rawData = receiptsRepository.findRevenueByAddress(startDate, endDate)

        val revenueByRegion = rawData.groupBy { obj ->
            val address = obj[0].toString()
            REGION_MAPPING.entries.find { address.startsWith(it.key) }?.value ?: "기타"
        }.mapValues { (_, value) -> value.sumOf { (it[1] as BigDecimal).toLong() } }

        val labels = revenueByRegion.keys.toList()
        val data = labels.map { revenueByRegion[it] ?: 0L }

        return AdminDataAnalyticsTO(labels = labels, data = data)
    }

    /** 날짜 범위 리스트 생성 */
    private fun generateDateRange(start: LocalDate, end: LocalDate, pattern: String, unit: ChronoUnit): List<String> {
        return (0..unit.between(start, end).toInt()).map { i ->
            start.plus(i.toLong(), unit).format(DateTimeFormatter.ofPattern(pattern))
        }
    }

    /** 매출 데이터 변환  */
    private fun formatSalesData(salesMap: Map<String, BigDecimal>, dateRange: List<String>): List<Map<String, Any>> {
        return dateRange.map { date ->
            mapOf("date" to date, "revenue" to salesMap.getOrDefault(date, BigDecimal.ZERO))
        }
    }

    /** 거래량 데이터 변환  */
    private fun formatTransactionCounts(transactions: List<AdminDashboardTO>, dateRange: List<String>): List<Map<String, Any>> {
        val transactionMap = transactions
            .groupingBy { it.receipts.reservedAt.toLocalDate().toString() }
            .eachCount()
            .mapValues { it.value.toLong() }

        return dateRange.map { date ->
            mapOf("date" to date, "count" to transactionMap.getOrDefault(date, 0L))
        }
    }

    private fun formatUserRegistrationCounts(
        rawData: List<Array<Any>>,
        dateRange: List<String>,
    ): List<Map<String, Any>> {
        val registrationMap = rawData.associate { it[0].toString() to (it[1] as Long) }

        return dateRange.map { date ->
            mapOf("date" to date, "count" to registrationMap.getOrDefault(date, 0L))
        }
    }

    private fun <K, V> convertToMap(rawData: List<Array<Any>>): Map<K, V> {
        return rawData.associate { it[0] as K to it[1] as V }
    }
}
