package com.project.popupmarket.controller.mypage

import com.project.popupmarket.dto.user.UserDto
import com.project.popupmarket.dto.user.UserUpdateRequest
import com.project.popupmarket.entity.User
import com.project.popupmarket.service.user.UserService
import lombok.RequiredArgsConstructor
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RequiredArgsConstructor
@Controller
class MyPageController {
    @Autowired
    private val userService: UserService? = null

    private val currentUser: UserDto
        // 공통으로 사용할 사용자 정보 조회 메서드
        get() {
            val modelMapper = ModelMapper()
            val authentication = SecurityContextHolder.getContext().authentication
            val userEmail = authentication.name

            val user = userService?.findByEmail(userEmail)
                ?: throw RuntimeException("사용자를 찾을 수 없습니다")

            return modelMapper.map(user, UserDto::class.java)
        }

    @get:ResponseBody
    @get:GetMapping("/api/user")
    val userInfo: ResponseEntity<UserDto?>
        get() {
            return try {
                ResponseEntity.ok(currentUser)
            } catch (e: Exception) {
                ResponseEntity.badRequest().body(null)
            }
        }

    @PutMapping("/api/updateUser")
    fun updateUser(
        @RequestPart(value = "userUpdateRequest") request: UserUpdateRequest,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?
    ): ResponseEntity<String?> {
        try {
            val currentUser = currentUser // 앞서 만든 getCurrentUser() 메서드 활용
            request.profileImage = profileImage
            userService!!.updateUser(currentUser.id!!, request)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/api/deleteUser")
    fun deleteUser(): ResponseEntity<String?> {
        try {
            val currentUser = currentUser
            userService!!.deleteUser(currentUser.id!!)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }
}
