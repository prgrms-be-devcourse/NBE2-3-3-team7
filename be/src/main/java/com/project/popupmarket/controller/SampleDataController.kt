package com.project.popupmarket.controller

import com.project.popupmarket.dto.land.RentalLandTO
import com.project.popupmarket.dto.popup.PopupTO
import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.AgeGroup
import com.project.popupmarket.enums.PopupType
import com.project.popupmarket.service.land.RentalLandService
import com.project.popupmarket.service.popup.PopupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random


@RestController
@RequestMapping("/api/sample")
class SampleDataController(
    private val rentalLandService: RentalLandService,
    private val popupService: PopupService
) {

    @PostMapping("/generate/land")
    fun generateSampleLand(
        @RequestPart(value = "thumbnail", required = false) thumbnail: MultipartFile,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<String> {

        for (i in 1 .. 100) {
            val rentalPlaceTO = RentalLandTO(
                landlordId = 2L,
                price = BigDecimal(100000),
                zipcode = "13617",
                address = "경기 성남시 분당구 미금로 246",
                addrDetail = "1001동 ${i}호",
                description = "설명입니다.",
                infra = "엘리베이터,와이파이,반려동물 허용,버스 정류장,지하철역,주차,에어컨",
                title = "분당 아파트 ${i}호",
                area = 30,
                ageGroup = AgeGroup.TEEN.desc,
                registeredAt = getRandomRegisteredAt()
            )

            rentalLandService.insert(rentalPlaceTO, thumbnail, images)
        }

        return ResponseEntity.ok("샘플 임대지 생성 완료")
    }

    @PostMapping("/generate/popup")
    fun generateSamplePopup(
        @RequestPart(value = "thumbnail", required = false) thumbnail: MultipartFile,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<String> {

        for (i in 1 .. 100) {

            val startDate = getRandomStartDate()
            val endDate = getRandomEndDate(startDate)

            val popupTO = PopupTO(
                customerId = 1L,
                type = getRandomPopupType().desc,
                zipcode = "13617",
                address = "경기 성남시 분당구 미금로 246",
                addrDetail = "1001동 ${i}호",
                title = "분당 아파트 ${i}호",
                description = "설명입니다.",
                infra = "엘리베이터,와이파이,반려동물 허용,버스 정류장,지하철역,주차,에어컨",
                startDate = startDate,
                endDate = endDate,
                ageGroup = AgeGroup.TEEN.desc,
                registeredAt = getRandomRegisteredAt()
            )

            popupService.createPopup(popupTO, thumbnail, images)
        }

        return ResponseEntity.ok("샘플 팝업 생성 완료")
    }

    fun getRandomStartDate(): LocalDate {
        return LocalDate.now().plusDays((1..30).random().toLong()) // 오늘부터 1~30일 후
    }

    fun getRandomEndDate(startDate: LocalDate): LocalDate {
        return startDate.plusDays((3..7).random().toLong()) // startDate 이후 3~7일 후
    }

    fun getRandomPopupType(): PopupType {
        val categories = PopupType.entries
        return categories[Random.nextInt(categories.size)]
    }

    fun getRandomRegisteredAt(): LocalDateTime {
        val daysAgo = Random.nextLong(1, 31) // 1~30일 전
        return LocalDateTime.now().minus(daysAgo, ChronoUnit.DAYS)
    }
}