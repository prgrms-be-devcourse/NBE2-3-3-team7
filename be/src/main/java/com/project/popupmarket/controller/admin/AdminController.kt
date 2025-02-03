package com.project.popupmarket.controller.admin

import com.project.popupmarket.dto.admin.AdminDashboardSummaryTO
import com.project.popupmarket.dto.admin.AdminPeriodAnalyticsTO
import com.project.popupmarket.dto.admin.AdminReceiptsDTO
import com.project.popupmarket.dto.land.RentalLandTO
import com.project.popupmarket.dto.popup.PopupTO
import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.ReservationStatus
import com.project.popupmarket.service.admin.AdminService
import com.project.popupmarket.service.land.RentalLandService
import com.project.popupmarket.service.popup.PopupService
import io.swagger.v3.oas.annotations.Operation
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val rentalLandService: RentalLandService,
    private val popupService: PopupService,
    private val adminService: AdminService,
) {

    // [ READ ] - 1: 조건에 해당하는 임대지 리스트 불러오기
    @GetMapping("/lands")
    @Operation(summary = "조건에 해당하는 임대지 리스트")
    fun getLandsByFilter(
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) status: ActivateStatus?,
        @RequestParam(required = false) sorting: String?,
        @RequestParam(required = false) title: String?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<RentalLandTO>> {
        val rentalLandTO = rentalLandService.findLandAdminByFilter(address, status, title, sorting, pageable)
        return ResponseEntity.ok(rentalLandTO)
    }

    // [ READ ] - 2: 조건에 해당하는 팝업 리스트 불러오기
    @GetMapping("/popups")
    @Operation(summary = "조건에 해당하는 팝업 리스트")
    fun getPopupsByFilter(
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) status: ActivateStatus?,
        @RequestParam(required = false) sorting: String?,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) type: String?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<PopupTO>> {
        val popupTO = popupService.findPopupAdminByFilter(address, status, title, type, sorting, pageable)
        return ResponseEntity.ok(popupTO)
    }

    // [ READ ] - 3: 조건에 해당하는 거래 내역 관리 리스트 불러오기
    @GetMapping("/receipts")
    @Operation(summary = "조건에 해당하는 거래 내역 관리 리스트")
    fun getReceiptsFilter(
        @RequestParam(required = false) status: ReservationStatus?,
        @RequestParam(required = false) sorting: String?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<AdminReceiptsDTO>> {
        val adminReceiptsDTOPage = adminService.getAdminReceiptsInfo(status, sorting, pageable)
        return ResponseEntity.ok(adminReceiptsDTOPage)
    }

    // [ READ ] - 4: 조건에 해당하는 메인 대시보드 불러오기
    @GetMapping("/dashboard")
    fun getWeeklyDashboard(): ResponseEntity<AdminDashboardSummaryTO> {
        return ResponseEntity.ok(adminService.getWeeklyDashboard())
    }

    @GetMapping("/analytics/daily")
    fun getDailyAnalytics(
        @RequestParam(defaultValue = "7") days: Int,
    ): ResponseEntity<AdminPeriodAnalyticsTO> {
        return ResponseEntity.ok(adminService.getDailyAnalytics(days - 1))
    }

    @GetMapping("/analytics/monthly")
    fun getMonthlyAnalytics(
        @RequestParam(defaultValue = "6") months: Int,
    ): ResponseEntity<AdminPeriodAnalyticsTO> {
        return ResponseEntity.ok(adminService.getMonthlyAnalytics(months - 1))
    }

    // [ Delete ] - 1: id에 해당하는 개별 임대지 삭제
    @DeleteMapping("/land/{id}")
    @Operation(summary = "개별 임대지 삭제")
    fun deleteLand(@PathVariable id: Long): ResponseEntity<Void> {
        rentalLandService.delete(id)
        return ResponseEntity.noContent().build()
    }

    // [ Delete ] - 2: id에 해당하는 개별 팝업 삭제
    @DeleteMapping("/popup/{id}")
    @Operation(summary = "개별 팝업 삭제")
    fun deletePopup(@PathVariable id: Long): ResponseEntity<Void> {
        popupService.deletePopupById(id)
        return ResponseEntity.noContent().build()
    }
}
