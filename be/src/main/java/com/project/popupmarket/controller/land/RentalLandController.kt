package com.project.popupmarket.controller.land

import com.project.popupmarket.dto.land.RentalLandRespTO
import com.project.popupmarket.dto.land.RentalLandTO
import com.project.popupmarket.service.land.RentalLandService
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
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
class RentalLandController (
    private val rentalLandService: RentalLandService,
    private val userContextUtil: UserContextUtil
) {

    // [ READ ] - 1
    // : 조건에 해당하는 팝업들 미리보기
    @GetMapping("/land/list")
    @Operation(summary = "조건에 해당하는 임대지 리스트")
    fun rentalListPagination( // 임대 리스트 페이지 9개 + 필터링 + 페이지네이션
        @RequestParam(required = false) minArea: Int?,  // 최소 면적 기본값 0
        @RequestParam(required = false) maxArea: Int?,  // 최소 면적 기본값 100
        @RequestParam(required = false) location: String?,  // 위치, 기본값 null
        @RequestParam(required = false) minPrice: BigDecimal?,  // 최소 가격 기본값 0
        @RequestParam(required = false) maxPrice: BigDecimal?,  // 최소 가격 기본값 10000000
        @RequestParam(required = false) startDate: LocalDate?,  // 시작일
        @RequestParam(required = false) endDate: LocalDate?,  // 종료일
        @RequestParam(required = false) sorting: String?,  // 정렬 기준
        @RequestParam(defaultValue = "0") page: Int
    ): Page<RentalLandRespTO> {
//        GET /list?page=0 -> 초기 값
//        GET /list?minArea=30&maxArea=70&location=서울&minPrice=100000&maxPrice=9000000&page=0
        //Capacity, Price, Name, Thumbnail

        val minA = minArea ?: 1
        val maxA = maxArea ?: 500
        val minP = minPrice ?: BigDecimal(1)
        val maxP = maxPrice ?: BigDecimal(10000000)

        val pageable: Pageable = PageRequest.of(page, 9)

        return rentalLandService.findByFilter(minA, maxA, location, minP, maxP, startDate, endDate, sorting, pageable)

    }

    // [ READ ] - 2
    // 특정 번호에 해당하는 임대지 상세 정보
    @GetMapping("/land/{id}")
    @Operation(summary = "개별 임대지 조회")
    fun rentalPlaceById(
        @PathVariable id: Long
    ): ResponseEntity<RentalLandRespTO> {
        return ResponseEntity.ok(rentalLandService.findRentalLandById(id))
    }

    // [ Read ] - 3 : 관리 중인 임대지 목록
    @GetMapping("/land/user")
    @Operation(summary = "사용자 임대지 리스트")
    fun userRentalList(): List<RentalLandRespTO> {
        val userSeq = userContextUtil.userId ?: throw IllegalStateException("사용자 ID가 필요합니다")

        return rentalLandService.findByUserId(userSeq)
    }

    // [ CREATE ]
    @Operation(summary = "임대지 추가")
    @PostMapping(value = ["/land"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun insertRentalPlace( // 임대페이지 데이터 create
        @RequestPart("rentalPlace") rentalPlaceTO: RentalLandTO,
        @RequestPart(value = "thumbnail", required = false) thumbnail: MultipartFile,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Void> {
        val userSeq = userContextUtil.userId
        rentalPlaceTO.landlordId = userSeq

        rentalLandService.insert(rentalPlaceTO, thumbnail, images)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    // [ Update ] - 1 : 개별 임대지 수정
    @PutMapping("/land/{id}")
    @Operation(summary = "개별 임대지 수정")
    fun updateRentalPlace(
        @PathVariable("id") id: Long,
        @RequestPart("rentalPlace") rentalPlaceTO: RentalLandTO,
        @RequestPart(value = "thumbnail", required = false) thumbnail: MultipartFile,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Void> {
        rentalPlaceTO.id = id
        rentalPlaceTO.landlordId = userContextUtil.userId

        rentalLandService.deleteImage(id)
        rentalLandService.insert(rentalPlaceTO, thumbnail, images)

        return ResponseEntity.noContent().build()
    }

    // [ Update ] - 2 : 임대지 상태 변경
    @PatchMapping("/land/{id}")
    @Operation(summary = "임대지 상태 변경 -> [ACTIVE, INACTIVE]")
    fun updateRentalStatus(
        @PathVariable("id") id: Long,
        @RequestBody status: String
    ): ResponseEntity<Void> {
        rentalLandService.updateStatus(id, status)
        return ResponseEntity.ok().build()
    }

    // [ Delete ]
    @DeleteMapping("/land/{id}")
    @Operation(summary = "개별 임대지 삭제")
    fun deleteRentalPlace(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        rentalLandService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
