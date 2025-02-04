package com.project.popupmarket.controller.user

import com.project.popupmarket.dto.user.UserDto
import com.project.popupmarket.dto.user.UserHomeResponse
import com.project.popupmarket.dto.user.UserRegisterDto
import com.project.popupmarket.dto.user.UserUpdateRequest
import com.project.popupmarket.enums.Role
import com.project.popupmarket.exception.ErrorCode
import com.project.popupmarket.exception.ErrorResponse
import com.project.popupmarket.service.aws.S3FileService
import com.project.popupmarket.service.land.RentalLandService
import com.project.popupmarket.service.popup.PopupService
import com.project.popupmarket.service.receipts.PaymentService
import com.project.popupmarket.service.user.UserService
import jakarta.servlet.http.HttpServletResponse
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class UserApiController(
    private val userService: UserService,
    private val rentalLandService: RentalLandService,
    private val paymentService: PaymentService,
    private val popupService: PopupService,
    private val s3FileService: S3FileService,
) {
    @PostMapping("/signup")
    fun signup(
        @RequestPart(value = "data") request: UserRegisterDto,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return try {
            userService.save(request, profileImage)

            ResponseEntity.ok("")
        } catch (e: Exception) {
            ResponseEntity
                .status(ErrorCode.SIGNUP_FAILED.status)
                .body(ErrorResponse(
                    message = ErrorCode.SIGNUP_FAILED.message,
                    code = ErrorCode.SIGNUP_FAILED.name
                ))
        }
    }

    @GetMapping("/user")
    fun getUserInfo(): ResponseEntity<UserDto> {
        return ResponseEntity.ok(getCurrentUser())
    }

    @PutMapping("/user")
    fun updateUser(
        @RequestPart(value = "data") request: UserUpdateRequest,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?
    ): ResponseEntity<String> {
        return try {
            request.profileImage = profileImage
            userService.updateUser(getCurrentUser().id!!, request)
            ResponseEntity.ok("")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/user")
    fun deleteUser(): ResponseEntity<String> {
        return try {
            userService.deleteUser(getCurrentUser().id!!)
            ResponseEntity.ok("")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/user/home")
    fun getUserHome(): ResponseEntity<UserHomeResponse> {
        val user = getCurrentUser()

        val landList = if (user.role == Role.LANDLORD) rentalLandService.findByLandlordIdWithLimit(user.id!!) else emptyList()
        val analytics = if (user.role == Role.LANDLORD) paymentService.getMonthlyAnalytics(user.id!!) else emptyList()
        val popupList = if (user.role == Role.CUSTOMER) popupService.findByCustomerIdWithLimit(user.id!!) else emptyList()
        val paymentList = if (user.role == Role.CUSTOMER) paymentService.getReceiptsInfoByCustomerIdWithLimit(user.id!!) else emptyList()

        println("""
            $user
            $landList
            $analytics
            $popupList
            $paymentList
        """)

        return ResponseEntity.ok(UserHomeResponse(
            user = user,
            land = landList,
            analytics = analytics,
            popup = popupList,
            payment = paymentList,
        ))
    }

    @PostMapping("/user/email")
    fun checkEmailValidate(@RequestBody user: UserDto): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.findByEmail(user.email!!) != null)
    }

    @PostMapping("/user/business")
    fun checkBusinessIdValidate(@RequestBody user: UserDto): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.findByBusinessId(user.businessId!!) != null)
    }

    private fun getCurrentUser(): UserDto {
        val modelMapper = ModelMapper()

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("인증 정보를 찾을 수 없습니다.")

        if (authentication.name == "anonymousUser") {
            throw RuntimeException("인증되지 않은 사용자입니다.")
        }

        val userEmail = authentication.name
        val user = userService.findByEmail(userEmail)
            ?: throw RuntimeException("사용자를 찾을 수 없습니다.")

        val userTo = modelMapper.map(user, UserDto::class.java)

        val profilePath = "user/${userTo.id}_thumbnail.png"
        val profileUrl = s3FileService.getCloudFrontImageUrl(profilePath)

        userTo.id?.let { id ->
            userTo.businessId = userService.findBusinessIdByUserId(id) ?: userTo.businessId
        }

        userTo.profileImage = profileUrl

        return userTo
    }
}