package com.project.popupmarket.controller.home

import com.project.popupmarket.dto.home.HomeItemTO
import com.project.popupmarket.service.land.RentalLandService
import com.project.popupmarket.service.popup.PopupService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HomeController(
    private val rentalLandService: RentalLandService,
    private val popupStoreService: PopupService,
) {

    @GetMapping("/home")
    @Operation(summary = "메인 페이지 최신 데이터 각각 10개 조회")
    fun getNewData(): ResponseEntity<HomeItemTO> {
        val popupList = popupStoreService.findWithLimit()
        val rentalLandList = rentalLandService.findByLimit()
        return ResponseEntity.ok(
            HomeItemTO(
                popupList,
                rentalLandList
            )
        )
    }

    @GetMapping("/test/data")
    fun getTestData(): ResponseEntity<Map<String, String>> {
        val map = mapOf(
            "이름" to "대호",
            "나이" to "25살",
            "성별" to "남성",
            "직업" to "무직"
        )
        return ResponseEntity.ok(map)
    }
}