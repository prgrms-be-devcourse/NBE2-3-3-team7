package com.project.popupmarket.controller.popup

import com.project.popupmarket.dto.popup.PopupRespTO
import com.project.popupmarket.dto.popup.PopupTO
import com.project.popupmarket.service.popup.PopupService
import com.project.popupmarket.util.UserContextUtil
import io.swagger.v3.oas.annotations.Operation
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
class PopupController(
    private val popupService: PopupService,
    private val userContextUtil: UserContextUtil
) {
    // : 조건에 해당하는 팝업들 미리보기
    @GetMapping("/popup/list")
    @Operation(summary = "조건에 해당하는 팝업 리스트")
    fun rentalListPagination( // 팝업 리스트 페이지 9개 + 필터링 + 페이지네이션
        @RequestParam(required = false) targetLocation: String?,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) targetAgeGroup: String?,
        @RequestParam(required = false) startDate: LocalDate?,  // 시작일
        @RequestParam(required = false) endDate: LocalDate?,  // 종료일
        @RequestParam(required = false) sorting: String?,  // 정렬 기준
        @RequestParam(defaultValue = "0") page: Int
    ): Page<PopupRespTO> {
        val pageable: Pageable = PageRequest.of(page, 9)

        return popupService.findFilterWithPagination(targetLocation, type, targetAgeGroup, startDate, endDate, sorting, pageable)
    }

    // [ READ ] - 2
    // 특정 번호에 해당하는 팝업 상세 정보
    @GetMapping("/popup/{id}")
    @Operation(summary = "개별 팝업 조회")
    fun popupById(
        @PathVariable id: Long
    ): ResponseEntity<PopupRespTO> {
        return ResponseEntity.ok(popupService.getUserWithImages(id))
    }

    // [ Read ] - 3 : 관리 중인 팝업 목록
    @GetMapping("/popup/user")
    @Operation(summary = "사용자 팝업 리스트")
    fun userPopupList(): List<PopupRespTO> {
        val userSeq = userContextUtil.userId

        return popupService.findPopupByUserId(userSeq)
    }

    // [ CREATE ]
    @Operation(summary = "팝업 추가")
    @PostMapping(value = ["/popup"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun insertRentalPlace( // 임대페이지 데이터 create
        @RequestPart("popup") popupTO: PopupTO,
        @RequestPart(value = "thumbnail", required = false) thumbnail: MultipartFile,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Void> {
        val userSeq = userContextUtil.userId
        popupTO.customerId = userSeq

        popupService.createPopup(popupTO, thumbnail, images)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    // [ Update ] - 1 : 개별 임대지 수정
    @PutMapping("/popup/{id}")
    @Operation(summary = "개별 임대지 수정")
    fun updatePopup(
        @PathVariable("id") id: Long,
        @RequestPart("popup") popupTO: PopupTO,
        @RequestPart(value = "thumbnail", required = false) thumbnail: MultipartFile,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Void> {
        popupTO.id = id
        popupTO.customerId = userContextUtil.userId

        popupService.deletePopupImageById(id)
        popupService.createPopup(popupTO, thumbnail, images)

        return ResponseEntity.noContent().build()
    }

    // [ Update ] - 2 : 임대지 상태 변경
    @PatchMapping("/popup/{id}")
    @Operation(summary = "팝업 상태 변경 -> [ACTIVE, INACTIVE]")
    fun updatePopupStatus(
        @PathVariable("id") id: Long,
        @RequestBody status: String
    ): ResponseEntity<Void> {
        popupService.updatePopupStatus(id, status)
        return ResponseEntity.ok().build()
    }

    // [ Delete ]
    @DeleteMapping("/popup/{id}")
    @Operation(summary = "개별 팝업 삭제")
    fun deletePopup(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        popupService.deletePopupById(id)
        return ResponseEntity.noContent().build()
    }
}