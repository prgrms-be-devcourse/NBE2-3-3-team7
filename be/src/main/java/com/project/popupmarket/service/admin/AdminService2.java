//package com.project.popupmarket.service.admin;
//
//import com.project.popupmarket.dto.admin.*;
//import com.project.popupmarket.dto.land.RentalLandTO;
//import com.project.popupmarket.dto.payment.ReceiptsManageTO;
//import com.project.popupmarket.dto.user.UserDto;
//import com.project.popupmarket.entity.Receipts;
//import com.project.popupmarket.entity.User;
//import com.project.popupmarket.enums.ActivateStatus;
//import com.project.popupmarket.enums.ReservationStatus;
//import com.project.popupmarket.enums.Role;
//import com.project.popupmarket.repository.*;
//import com.project.popupmarket.service.land.RentalLandService;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//@Service
//@RequiredArgsConstructor
//public class AdminService2 {
//
//    private final UserRepository userRepository;
//    private final AdminRepository adminRepository;
//    private final RentalLandService rentalLandService;
//    private final ReceiptsRepository receiptsRepository;
//    private final RentalLandJpaRepository rentalLandJpaRepository;
//    private final PopupJpaRepository popupJpaRepository;
//
//    public UserDto findReceiptsAdminById(Long userId) {
//        ModelMapper modelMapper = new ModelMapper();
//        User user = userRepository.findUserInfoById(userId);
//        return modelMapper.map(user, UserDto.class);
//    }
//
//    public Page<AdminReceiptsDTO> getAdminReceiptsInfo (ReservationStatus reservation_status, String sorting, Pageable pageable) {
//        return adminRepository.findReceiptsByFilter(reservation_status, sorting, pageable)
//                .map(receipt -> {
//                    ReceiptsManageTO receiptsManageTO = ReceiptsManageTO.builder()
//                            .paymentKey(receipt.getPaymentKey())
//                            .amount(receipt.getAmount())
//                            .start(receipt.getStartDate())
//                            .end(receipt.getEndDate())
//                            .status(receipt.getReservationStatus())
//                            .reservedAt(receipt.getReservedAt())
//                            .build();
//
//                    UserDto user = findReceiptsAdminById(receipt.getCustomerId());
//                    RentalLandTO rentalLandTO = rentalLandService.findById(receipt.getRentalLandId());
//                    return new AdminReceiptsDTO(receiptsManageTO, rentalLandTO, user);
//                });
//    }
//
//    public AdminDashboardSummaryTO getWeeklyDashboard() {
//        LocalDateTime startDate = LocalDateTime.now().minusDays(6);
//        LocalDateTime endDate = LocalDateTime.now();
//
//        // 거래 내역 데이터 조회 및 변환
//        List<Object[]> dashboardData = receiptsRepository.findDashboardsBetween(startDate, endDate);
//        List<AdminDashboardTO> dashboardList = dashboardData.stream()
//                .map(obj -> new AdminDashboardTO(
//                        ReceiptsManageTO.builder()
//                                .paymentKey(((Receipts) obj[0]).getPaymentKey())
//                                .amount(((Receipts) obj[0]).getAmount())
//                                .start(((Receipts) obj[0]).getStartDate())
//                                .end(((Receipts) obj[0]).getEndDate())
//                                .status(((Receipts) obj[0]).getReservationStatus())
//                                .reservedAt(((Receipts) obj[0]).getReservedAt())
//                                .build(),
//                        (String) obj[1],  // Popup 이름
//                        (String) obj[2]   // RentalLand 이름
//                ))
//                .toList();
//
//        // 일일 거래량 데이터
//        List<String> lastWeekDates = generateDateRange(startDate.toLocalDate(), endDate.toLocalDate(), "yyyy-MM-dd", ChronoUnit.DAYS);
//        List<Map<String, Object>> dailyCounts = formatTransactionCounts(dashboardList, lastWeekDates);
//
//        // 일일 가입자 수
//        List<Object[]> weeklyRegistered = userRepository.findUsersByRegisteredAtBetween(startDate, endDate);
//        List<Map<String, Object>> formattedWeeklyRegistered = formatUserRegistrationCounts(weeklyRegistered, lastWeekDates);
//
//        // 통계 데이터
//        Map<Role, Long> countUsersByRoleMap = convertToMap(userRepository.countUsersByRole());
//        Map<ActivateStatus, Long> totalRentalLandsMap = convertToMap(rentalLandJpaRepository.countRentalLandsByStatus());
//        Map<ActivateStatus, Long> totalPopupsMap = convertToMap(popupJpaRepository.countPopupsByStatus());
//
//        // 월/주/일 매출 통계
//        AdminMonthlyCountTO adminMonthlyCountTO = AdminMonthlyCountTO.builder()
//                .monthlyCount(receiptsRepository.findTotalCountByBetween(startDate.minusMonths(1), endDate))
//                .monthly(receiptsRepository.findTotalAmountByBetween(startDate.minusMonths(1), endDate))
//                .weekly(receiptsRepository.findTotalAmountByBetween(startDate.minusWeeks(1), endDate))
//                .daily(receiptsRepository.findTotalAmountByBetween(startDate.minusDays(1), endDate))
//                .build();
//
//        return AdminDashboardSummaryTO.builder()
//                .dashboardList(dashboardList)
//                .dailyCounts(dailyCounts)
//                .weeklyRegistered(formattedWeeklyRegistered)
//                .countUsers(countUsersByRoleMap)
//                .totalLands(totalRentalLandsMap)
//                .totalPopups(totalPopupsMap)
//                .monthlyCount(adminMonthlyCountTO)
//                .build();
//    }
//
//    public AdminPeriodAnalyticsTO getDailyAnalytics(int days){
//        // 현재 주간 데이터 날짜 범위 설정
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = endDate.minusDays(days);
//
//        // 이전 주간 데이터 날짜 범위 설정
//        LocalDateTime previousEndDate = startDate.minusDays(1);
//        LocalDateTime previousStartDate = previousEndDate.minusDays(days);
//
//        // 날짜 범위 생성 (MM-dd 형식)
//        List<String> dateRange = generateDateRange(startDate.toLocalDate(), endDate.toLocalDate(), "MM-dd", ChronoUnit.DAYS);
//
//        // 데이터 맵으로 변환
//        Map<String, BigDecimal> currentSalesMap = convertToMap(receiptsRepository.findDailySalesBetween(startDate, endDate));
//        Map<String, BigDecimal> previousSalesMap = convertToMap(receiptsRepository.findDailySalesBetween(previousStartDate, previousEndDate));
//
//        // 데이터 리스트 변환
//        List<Map<String, Object>> formattedCurrentSales = formatSalesData(currentSalesMap, dateRange);
//        List<Map<String, Object>> formattedPreviousSales = formatSalesData(previousSalesMap, dateRange);
//
//        return AdminPeriodAnalyticsTO.builder()
//                .current(formattedCurrentSales)
//                .prev(formattedPreviousSales)
//                .data(getRevenueByRegion(startDate, endDate))
//                .build();
//    }
//
//    public AdminPeriodAnalyticsTO getMonthlyAnalytics(int months){
//        LocalDateTime endMonth = LocalDateTime.now();
//        LocalDateTime startMonth = endMonth.minusMonths(months);
//
//        LocalDateTime previousEndMonth = startMonth.minusMonths(1);
//        LocalDateTime previousStartMonth = previousEndMonth.minusMonths(months);
//
//        // 날짜 범위 생성 (MM-dd 형식)
//        List<String> monthsRange = generateDateRange(startMonth.toLocalDate(), endMonth.toLocalDate(), "MM", ChronoUnit.MONTHS);
//
//        // 데이터 맵으로 변환
//        Map<String, BigDecimal> currentSalesMap = convertToMap(receiptsRepository.findMonthlySalesBetween(startMonth, endMonth));
//        Map<String, BigDecimal> previousSalesMap = convertToMap(receiptsRepository.findMonthlySalesBetween(previousStartMonth, previousEndMonth));
//
//        // 데이터 리스트 변환
//        List<Map<String, Object>> formattedCurrentSales = formatSalesData(currentSalesMap, monthsRange);
//        List<Map<String, Object>> formattedPreviousSales = formatSalesData(previousSalesMap, monthsRange);
//
//        return AdminPeriodAnalyticsTO.builder()
//                .current(formattedCurrentSales)
//                .prev(formattedPreviousSales)
//                .data(getRevenueByRegion(startMonth, endMonth))
//                .build();
//    }
//
//    private static final Map<String, String> REGION_MAPPING = Map.ofEntries(
//            Map.entry("서울", "서울"), Map.entry("부산", "부산"), Map.entry("대구", "대구"), Map.entry("인천", "인천"),
//            Map.entry("광주", "광주"), Map.entry("대전", "대전"), Map.entry("울산", "울산"), Map.entry("세종", "세종"),
//            Map.entry("경기", "경기"), Map.entry("충북", "충북"), Map.entry("충남", "충남"), Map.entry("전북", "전북"),
//            Map.entry("전남", "전남"), Map.entry("경북", "경북"), Map.entry("경남", "경남"), Map.entry("강원", "강원"),
//            Map.entry("제주", "제주")
//    );
//
////    public List<Map<String, Object>> getRevenueByRegion(LocalDateTime startDate, LocalDateTime endDate) {
//    public AdminDataAnalyticsTO getRevenueByRegion(LocalDateTime startDate, LocalDateTime endDate) {
//
//        List<Object[]> rawData = receiptsRepository.findRevenueByAddress(startDate, endDate);
//
//        Map<String, Long> revenueByRegion = rawData.stream()
//                .collect(Collectors.groupingBy(obj -> {
//                    String address = obj[0].toString();
//                    return REGION_MAPPING.entrySet().stream()
//                            .filter(entry -> address.startsWith(entry.getKey()))
//                            .map(Map.Entry::getValue)
//                            .findFirst()
//                            .orElse("기타");
//                }, Collectors.summingLong(obj -> ((BigDecimal) obj[1]).longValue())));
//
//        List<String> labels = new ArrayList<>(revenueByRegion.keySet());
//
//        List<Long> data = labels.stream()
//                .map(revenueByRegion::get)
//                .collect(Collectors.toList());
//
//        return AdminDataAnalyticsTO.builder()
//                .labels(labels)
//                .data(data)
//                .build();
//
//    }
//    public void getCategoryAnalytics(){
//
//    }
//
//    /** 날짜 범위 리스트 생성 */
//    private List<String> generateDateRange(LocalDate start, LocalDate end, String pattern, ChronoUnit unit) {
//        return IntStream.rangeClosed(0, (int) unit.between(start, end))
//                .mapToObj(i -> start.plus(i, unit).format(DateTimeFormatter.ofPattern(pattern)))
//                .toList();
//    }
//
//    /** 매출 데이터 변환 */
//    private List<Map<String, Object>> formatSalesData(Map<String, BigDecimal> salesMap, List<String> dateRange) {
//        return dateRange.stream()
//                .map(date -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("date", date);
//                    map.put("revenue", salesMap.getOrDefault(date, BigDecimal.ZERO));
//                    return map;
//                })
////                .map(date -> Map.of("date", date, "revenue", salesMap.getOrDefault(date, BigDecimal.ZERO)))
//                .toList();
//    }
//
//    ---
//
//    /** 거래량 데이터 변환 */
//    private List<Map<String, Object>> formatTransactionCounts(List<AdminDashboardTO> transactions, List<String> dateRange) {
//        Map<String, Long> transactionMap = transactions.stream()
//                .collect(Collectors.groupingBy(item -> item.getReceipts().getReservedAt().toLocalDate().toString(), Collectors.counting()));
//
//        return dateRange.stream()
//                .map(date -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("date", date);
//                    map.put("count", transactionMap.getOrDefault(date, 0L));
//                    return map;
//                })
////                .map(date -> Map.of("date", date, "count", transactionMap.getOrDefault(date, 0L)))
//                .toList();
//    }
//
//
//    ---
//    /** 사용자 가입 데이터 변환 */
//    private List<Map<String, Object>> formatUserRegistrationCounts(List<Object[]> rawData, List<String> dateRange) {
//        Map<String, Long> registrationMap = rawData.stream()
//                .collect(Collectors.toMap(obj -> obj[0].toString(), obj -> (Long) obj[1]));
//
//        return dateRange.stream()
//                .map(date -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("date", date);
//                    map.put("count", registrationMap.getOrDefault(date, 0L));
//                    return map;
//                })
////                .map(date -> Map.of("date", date, "count", registrationMap.getOrDefault(date, 0L)))
//                .toList();
//    }
//
//    /** 데이터 변환 (Role & ActivateStatus) */
//    private <K, V> Map<K, V> convertToMap(List<Object[]> rawData) {
//        return rawData.stream().collect(
//                Collectors.toMap(
//                        obj -> (K) obj[0],
//                        obj -> (V) obj[1]
//                ));
//    }
//}
