package com.project.popupmarket.controller.admin;

//import com.project.popupmarket.dto.admin.AdminDashboardTO;
import com.project.popupmarket.dto.admin.AdminDashboardSummaryTO;
import com.project.popupmarket.dto.admin.AdminDashboardTO;
import com.project.popupmarket.dto.admin.AdminPeriodAnalyticsTO;
import com.project.popupmarket.dto.admin.AdminReceiptsDTO;
import com.project.popupmarket.dto.land.RentalLandTO;
import com.project.popupmarket.dto.popup.PopupTO;
import com.project.popupmarket.enums.ActivateStatus;
import com.project.popupmarket.enums.ReservationStatus;
import com.project.popupmarket.service.admin.AdminService;
import com.project.popupmarket.service.land.RentalLandService;
import com.project.popupmarket.service.popup.PopupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final RentalLandService rentalLandService;
    private final PopupService popupService;
    private final AdminService adminService;

    // [ READ ] - 1
    // : 조건에 해당하는 임대지 리스트 불러오기
    @GetMapping("/lands")
    @Operation(summary = "조건에 해당하는 임대지 리스트")
    public ResponseEntity<Page<RentalLandTO>> getLandsByFilter(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) ActivateStatus status,
            @RequestParam(required = false) String sorting,
            @RequestParam(required = false) String title,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<RentalLandTO> rentalLandTO = rentalLandService.findLandAdminByFilter(address, status, title, sorting, pageable);
        return ResponseEntity.ok(rentalLandTO);
    }

    // [ READ ] - 2
    // : 조건에 해당하는 팝업 리스트 불러오기
    @GetMapping("/popups")
    @Operation(summary = "조건에 해당하는 팝업 리스트")
    public ResponseEntity<Page<PopupTO>> getPopupsByFilter(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) ActivateStatus status,
            @RequestParam(required = false) String sorting,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<PopupTO> popupTO = popupService.findPopupAdminByFilter(address, status, title, type, sorting, pageable);
        return ResponseEntity.ok(popupTO);
    }

    // [ READ ] - 3
    // : 조건에 해당하는 거래 내역 관리 리스트 불러오기
    @GetMapping("/receipts")
    @Operation(summary = "조건에 해당하는 거래 내역 관리 리스트")
    public ResponseEntity<Page<AdminReceiptsDTO>> getReceiptsFilter(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) String sorting,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AdminReceiptsDTO> adminReceiptsDTOPage = adminService.getAdminReceiptsInfo(status, sorting, pageable);
        return ResponseEntity.ok(adminReceiptsDTOPage);
    }

    // [ READ ] - 4
    // : 조건에 해당하는 메인 대시보드 불러오기
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardSummaryTO> getWeeklyDashboard() {
        return ResponseEntity.ok(adminService.getWeeklyDashboard());
    }

    @GetMapping("/analytics/daily")
    public ResponseEntity<AdminPeriodAnalyticsTO> getDailyAnalytics(
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(adminService.getDailyAnalytics(days - 1));
    }
    @GetMapping("/analytics/monthly")
    public ResponseEntity<AdminPeriodAnalyticsTO> getMonthlyAnalytics(
            @RequestParam(defaultValue = "6") int months
    ) {
        return ResponseEntity.ok(adminService.getMonthlyAnalytics(months - 1));
    }

    // [ Delete ] - 1 
    // : id에 해당하는 개별 임대지 삭제
    @DeleteMapping("/land/{id}")
    @Operation(summary = "개별 임대지 삭제")
    public ResponseEntity<Void> deleteLand(@PathVariable Long id) {
        rentalLandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // [ Delete ] - 2 
    // : id에 해당하는 개별 팝업 삭제
    @DeleteMapping("/popup/{id}")
    @Operation(summary = "개별 팝업 삭제")
    public ResponseEntity<Void> deletePopup(@PathVariable Long id) {
        popupService.deletePopupById(id);
        return ResponseEntity.noContent().build();
    }

    
}
