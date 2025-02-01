package com.project.popupmarket.service.admin;

//import com.project.popupmarket.dto.admin.AdminDashboardTO;
import com.project.popupmarket.dto.admin.*;
import com.project.popupmarket.dto.land.RentalLandTO;
import com.project.popupmarket.dto.payment.ReceiptsManageTO;
import com.project.popupmarket.dto.user.UserDto;
import com.project.popupmarket.entity.Receipts;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.enums.ActivateStatus;
import com.project.popupmarket.enums.ReservationStatus;
import com.project.popupmarket.enums.Role;
import com.project.popupmarket.repository.*;
import com.project.popupmarket.service.land.RentalLandService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RentalLandService rentalLandService;
    private final ReceiptsRepository receiptsRepository;
    private final RentalLandJpaRepository rentalLandJpaRepository;
    private final PopupJpaRepository popupJpaRepository;

    public UserDto findReceiptsAdminById(Long userId) {
        ModelMapper modelMapper = new ModelMapper();
        User user = userRepository.findUserInfoById(userId);
        return modelMapper.map(user, UserDto.class);
    }

    public Page<AdminReceiptsDTO> getAdminReceiptsInfo (ReservationStatus reservation_status, String sorting, Pageable pageable) {
        return adminRepository.findReceiptsByFilter(reservation_status, sorting, pageable)
                .map(receipt -> {
                    ReceiptsManageTO receiptsManageTO = ReceiptsManageTO.builder()
                            .paymentKey(receipt.getPaymentKey())
                            .amount(receipt.getAmount())
                            .start(receipt.getStartDate())
                            .end(receipt.getEndDate())
                            .status(receipt.getReservationStatus())
                            .reservedAt(receipt.getReservedAt())
                            .build();

                    UserDto user = findReceiptsAdminById(receipt.getCustomerId());
                    RentalLandTO rentalLandTO = rentalLandService.findById(receipt.getRentalLandId());
                    return new AdminReceiptsDTO(receiptsManageTO, rentalLandTO, user);
                });
    }

    public AdminDashboardSummaryTO getWeeklyDashboard(){

        List<Object[]> dashboardData = receiptsRepository.findDashboardsBetween(LocalDateTime.now().minusDays(6), LocalDateTime.now());
        List<AdminDashboardTO> dashboardList = dashboardData.stream()
                .map(obj -> {
                    Receipts receipts = (Receipts) obj[0];  // Receipts 엔티티
                    String customerName = (String) obj[1];  // Popup에서 가져온 customerName
                    String landlordName = (String) obj[2];  // RentalLand에서 가져온 landlordName

                    ReceiptsManageTO receiptsManageTO = ReceiptsManageTO.builder()
                            .paymentKey(receipts.getPaymentKey())
                            .amount(receipts.getAmount())
                            .start(receipts.getStartDate())
                            .end(receipts.getEndDate())
                            .status(receipts.getReservationStatus())
                            .reservedAt(receipts.getReservedAt())
                            .build();

                    return new AdminDashboardTO(receiptsManageTO, customerName, landlordName);
                })
                .collect(Collectors.toList());

        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 지난 6일 전 날짜부터 오늘까지의 범위 생성
        List<LocalDate> lastWeekDates = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> today.minusDays(6 - i))
                .toList();

        // 기존 데이터 그룹화
        Map<LocalDate, Long> transactionMap = dashboardList.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getReceipts().getReservedAt().toLocalDate(),
                        Collectors.counting()
                ));

        // 모든 날짜를 포함하여 데이터 생성 (없는 날짜는 0)
        List<Map<String, Object>> dailyCounts = lastWeekDates.stream()
                .map(date -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", date);
                    map.put("count", transactionMap.getOrDefault(date, 0L));
                    return map;
                })
                .toList();

        // DB에서 가져온 회원 등록 데이터
        List<Object[]> weeklyRegistered = userRepository.findUsersByRegisteredAtBetween(
                LocalDateTime.now().minusDays(6), LocalDateTime.now()
        );

        // 데이터를 Map<LocalDate, Long> 형태로 변환
        Map<LocalDate, Long> registeredMap = weeklyRegistered.stream()
                .collect(Collectors.toMap(
                        obj -> LocalDate.parse(obj[0].toString()), // 날짜 (String -> LocalDate 변환)
                        obj -> (Long) obj[1] // 등록된 사용자 수
                ));

        // 모든 날짜를 포함하여 결과 생성 (없는 날짜는 0)
        List<Map<String, Object>> formattedWeeklyRegistered = lastWeekDates.stream()
                .map(date -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", date);
                    map.put("count", registeredMap.getOrDefault(date, 0L));
                    return map;
                })
                .toList();

        Map<Role, Long> countUsersByRoleMap = userRepository.countUsersByRole().stream()
                .collect(Collectors.toMap(
                        obj -> (Role) obj[0],
                        obj -> ((Number) obj[1]).longValue()
                ));

        Map<ActivateStatus, Long> totalRentalLandsMap = rentalLandJpaRepository.countRentalLandsByStatus().stream()
                .collect(Collectors.toMap(
                        obj -> (ActivateStatus) obj[0],
                        obj -> ((Number) obj[1]).longValue()
                ));

        Map<ActivateStatus, Long> totalPopupsMap = popupJpaRepository.countPopupsByStatus().stream()
                .collect(Collectors.toMap(
                        obj -> (ActivateStatus) obj[0],
                        obj -> ((Number) obj[1]).longValue()
                ));

        AdminMonthlyCountTO adminMonthlyCountTO = AdminMonthlyCountTO.builder()
                .monthlyCount(receiptsRepository.findTotalCountByBetween(LocalDateTime.now().minusMonths(1),LocalDateTime.now()))
                .monthly(receiptsRepository.findTotalAmountByBetween(LocalDateTime.now().minusMonths(1),LocalDateTime.now()))
                .weekly(receiptsRepository.findTotalAmountByBetween(LocalDateTime.now().minusWeeks(1),LocalDateTime.now()))
                .daily(receiptsRepository.findTotalAmountByBetween(LocalDateTime.now().minusDays(1),LocalDateTime.now()))
                .build();

        return AdminDashboardSummaryTO.builder()
                .dashboardList(dashboardList)
                .dailyCounts(dailyCounts)
                .weeklyRegistered(formattedWeeklyRegistered)
                .countUsers(countUsersByRoleMap)
                .totalLands(totalRentalLandsMap)
                .totalPopups(totalPopupsMap)
                .monthlyCount(adminMonthlyCountTO)
                .build();
    }

    public AdminPeriodAnalyticsTO getDailyAnalytics(int days){
        // 현재 주간 데이터 날짜 범위 설정
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        List<Object[]> currentSales = receiptsRepository.findDailySalesBetween(startDate, endDate);

        // 이전 주간 데이터 날짜 범위 설정
        LocalDateTime previousEndDate = startDate.minusDays(1);
        LocalDateTime previousStartDate = previousEndDate.minusDays(days);
        List<Object[]> previousSales = receiptsRepository.findDailySalesBetween(previousStartDate, previousEndDate);

        // 날짜 범위 생성 (MM-dd 형식)
        List<String> currentdateRange = IntStream.rangeClosed(0, (int) (endDate.toLocalDate().toEpochDay() - startDate.toLocalDate().toEpochDay()))
                .mapToObj(i -> startDate.plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd")))
                .toList();

        List<String> previousdateRange = IntStream.rangeClosed(0, (int) (previousEndDate.toLocalDate().toEpochDay() - previousStartDate.toLocalDate().toEpochDay()))
                .mapToObj(i -> previousStartDate.plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd")))
                .toList();

        // 현재 주간 데이터 맵으로 변환
        Map<String, BigDecimal> currentSalesMap = currentSales.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(), // 날짜 ("MM-dd")
                        obj -> (BigDecimal) obj[1]
                ));

        // 이전 주간 데이터 맵으로 변환
        Map<String, BigDecimal> previousSalesMap = previousSales.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(), // 날짜 ("MM-dd")
                        obj -> (BigDecimal) obj[1]
                ));

        // 현재 주간 데이터 리스트 변환
        List<Map<String, Object>> formattedCurrentSales = currentdateRange.stream()
                .map(month -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", month);
                    map.put("revenue", currentSalesMap.getOrDefault(month, BigDecimal.ZERO));
                    return map;
                })
                .toList();

        // 이전 주간 데이터 리스트 변환
        List<Map<String, Object>> formattedPreviousSales = previousdateRange.stream()
                .map(month -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", month);
                    map.put("revenue", previousSalesMap.getOrDefault(month, BigDecimal.ZERO));
                    return map;
                })
                .toList();

        return AdminPeriodAnalyticsTO.builder()
                .current(formattedCurrentSales)
                .prev(formattedPreviousSales)
                .build();
    }

    public AdminPeriodAnalyticsTO getMonthlyAnalytics(int months){
        LocalDateTime endMonth = LocalDateTime.now();
        LocalDateTime startMonth = endMonth.minusMonths(months);
        List<Object[]> currentSales = receiptsRepository.findMonthlySalesBetween(startMonth, endMonth);

        LocalDateTime previousEndMonth = startMonth.minusMonths(1);
        LocalDateTime previousStartMonth = previousEndMonth.minusMonths(months);
        List<Object[]> previousSales = receiptsRepository.findMonthlySalesBetween(previousStartMonth, previousEndMonth);

        List<String> currentMonthsRange = IntStream.rangeClosed(0, (int) ChronoUnit.MONTHS.between(startMonth, endMonth))
                .mapToObj(i -> String.format("%02d월", startMonth.plusMonths(i).getMonthValue()))
                .toList();

        List<String> previousMonthsRange = IntStream.rangeClosed(0, (int) ChronoUnit.MONTHS.between(previousStartMonth, previousEndMonth))
                .mapToObj(i -> String.format("%02d월", previousStartMonth.plusMonths(i).getMonthValue()))
                .toList();

        Map<String, BigDecimal> currentSalesMap = currentSales.stream()
                .collect(Collectors.toMap(
                        obj -> String.format("%02d월", Integer.parseInt(obj[0].toString())),
                        obj -> (BigDecimal) obj[1]
                ));

        Map<String, BigDecimal> previousSalesMap = previousSales.stream()
                .collect(Collectors.toMap(
                        obj -> String.format("%02d월", Integer.parseInt(obj[0].toString())),
                        obj -> (BigDecimal) obj[1]
                ));

        List<Map<String, Object>> formattedCurrentSales = currentMonthsRange.stream()
                .map(month -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", month);
                    map.put("revenue", currentSalesMap.getOrDefault(month, BigDecimal.ZERO));
                    return map;
                })
                .toList();

        List<Map<String, Object>> formattedPreviousSales = previousMonthsRange.stream()
                .map(month -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", month);
                    map.put("revenue", previousSalesMap.getOrDefault(month, BigDecimal.ZERO));
                    return map;
                })
                .toList();

        return AdminPeriodAnalyticsTO.builder()
                .current(formattedCurrentSales)
                .prev(formattedPreviousSales)
                .build();
    }
}
