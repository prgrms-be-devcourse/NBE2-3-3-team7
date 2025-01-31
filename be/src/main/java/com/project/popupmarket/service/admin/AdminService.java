package com.project.popupmarket.service.admin;

//import com.project.popupmarket.dto.admin.AdminDashboardTO;
import com.project.popupmarket.dto.admin.AdminDashboardSummaryTO;
import com.project.popupmarket.dto.admin.AdminDashboardTO;
import com.project.popupmarket.dto.admin.AdminMonthlyCountTO;
import com.project.popupmarket.dto.admin.AdminReceiptsDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        List<Object[]> dashboardData = receiptsRepository.findDashboardsBetween(LocalDateTime.now().minusDays(30), LocalDateTime.now());
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
        List<Object[]> dailyCounts = lastWeekDates.stream()
                .map(date -> new Object[]{date.toString(), transactionMap.getOrDefault(date, 0L)})
                .toList();


//        List<Object[]> weeklyRegistered = userRepository.findUsersByRegisteredAtBetween(LocalDateTime.now().minusDays(7),LocalDateTime.now());

        // DB에서 가져온 회원 등록 데이터
        List<Object[]> weeklyRegistered = userRepository.findUsersByRegisteredAtBetween(
                LocalDateTime.now().minusDays(7), LocalDateTime.now()
        );

        // 데이터를 Map<LocalDate, Long> 형태로 변환
        Map<LocalDate, Long> registeredMap = weeklyRegistered.stream()
                .collect(Collectors.toMap(
                        obj -> LocalDate.parse(obj[0].toString()), // 날짜 (String -> LocalDate 변환)
                        obj -> (Long) obj[1] // 등록된 사용자 수
                ));

        // 모든 날짜를 포함하여 결과 생성 (없는 날짜는 0)
        List<Object[]> formattedWeeklyRegistered = lastWeekDates.stream()
                .map(date -> new Object[]{date.toString(), registeredMap.getOrDefault(date, 0L)})
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

        AdminDashboardSummaryTO dashboardSummaryTO = AdminDashboardSummaryTO.builder()
                .dashboardList(dashboardList)
                .dailyCounts(dailyCounts)
                .weeklyRegistered(formattedWeeklyRegistered)
                .countUsersByRole(countUsersByRoleMap)
                .totalRentalLands(totalRentalLandsMap)
                .totalPopups(totalPopupsMap)
                .monthlyCount(adminMonthlyCountTO)
                .build();

        return dashboardSummaryTO;
    }


}
