package com.project.popupmarket.controller.token

import com.project.popupmarket.dto.token.CreateAccessTokenRequest
import com.project.popupmarket.dto.token.CreateAccessTokenResponse
import com.project.popupmarket.service.token.TokenService
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RequiredArgsConstructor
@RestController
class TokenApiController {
    @Autowired
    private val tokenService: TokenService? = null

    @PostMapping("/api/token")
    fun createNewAccessToken(@RequestBody request: CreateAccessTokenRequest): ResponseEntity<CreateAccessTokenResponse> {
        val newAccessToken = tokenService!!.createNewAccessToken(request.accessToken)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateAccessTokenResponse(newAccessToken))
    }
}