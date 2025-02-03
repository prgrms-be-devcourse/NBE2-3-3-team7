package com.project.popupmarket.service.popup

import com.project.popupmarket.dto.land.RentalLandTO
import com.project.popupmarket.dto.popup.PopupRespTO
import com.project.popupmarket.dto.popup.PopupTO
import com.project.popupmarket.entity.Popup
import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.enums.AgeGroup
import com.project.popupmarket.exception.custom.ResourceNotFoundException
import com.project.popupmarket.exception.custom.S3Exception
import com.project.popupmarket.repository.PopupJpaRepository
import com.project.popupmarket.service.aws.S3FileService
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Service
class PopupService(
    private val popupJpaRepository: PopupJpaRepository,
    private val s3FileService: S3FileService,
) {
    // 해당 번호 팝업 찾아 TO로 반환
    fun findById(id: Long): PopupTO {
        return popupJpaRepository.findById(id)
            .map { popup ->
                ModelMapper().map(popup, PopupTO::class.java)
            }
            .orElseThrow { ResourceNotFoundException("$id 의 팝업 게시글이 없습니다.") }
    }

    // 1. GET Popup By Id : 임대지 상세 정보
    fun getUserWithImages(id: Long): PopupRespTO {
        return popupJpaRepository.findById(id)
            .map { popup: Popup ->
                val popupTO = ModelMapper().map(popup, PopupTO::class.java)

                val thumbnailFilePath = "popup/${popupTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val filePath = "popup/${popupTO.id}_images_"
                val imageUrls = s3FileService.getCloudFrontImageListUrl(filePath)
                println(filePath)
                println(imageUrls)

                val respTo = PopupRespTO()
                respTo.popup = popupTO
                respTo.thumbnail = thumbnailUrl
                respTo.images = imageUrls
                respTo
            }
            .orElseThrow { ResourceNotFoundException("$id 의 팝업 게시글이 없습니다.") }
    }

    // 2. GET Filtered Popup
    fun findFilterWithPagination(
        targetLocation: String?, type: String?, targetAgeGroup: String?,
        startDate: LocalDate?, endDate: LocalDate?, sorting: String?,
        pageable: Pageable?,
    ): Page<PopupRespTO> {
        val popupRespTO = popupJpaRepository
            .findFilteredWithPagination(
                targetLocation, type, targetAgeGroup, startDate, endDate,
                sorting, pageable
            )
            .map { po: Popup ->
                mappedPopupRespTO(po)
            }

        return popupRespTO
    }

    // 3. GET User's Popup
    fun findPopupByUserId(userId: Long, pageable: Pageable?): Page<PopupRespTO> {
        val popupRespTO = popupJpaRepository.findPopupByUserIdWithPagination(userId, pageable)
            .map { po ->
                mappedPopupRespTO(po)
            }

        return popupRespTO
    }

    // 4. GET Main Popup
    fun findWithLimit(): List<PopupRespTO> {
        val popupRespTO = popupJpaRepository.findWithLimit()
            .map { po ->
                mappedPopupRespTO(po)
            }

        return popupRespTO
    }

    fun findByCustomerIdWithLimit(customerId: Long): List<PopupRespTO> {
        val popupRespTO = popupJpaRepository.findByCustomerIdWithLimit(customerId)
            .map { po ->
                mappedPopupRespTO(po)
            }

        return popupRespTO
    }

    // 2 - 5. Read : 관리자 페이지 조건에 해당하는 팝업 20개 조회 + 검색 포함
    // TODO : 추후 Admin Migration 완료 이후에 'AdminService' 로 이동
    fun findPopupAdminByFilter(
        address: String?, status: ActivateStatus?,
        title: String?, type: String?,
        sorting: String?, pageable: Pageable?
    ): Page<PopupTO> {
        val modelMapper = ModelMapper()
        println(popupJpaRepository.findPopupAdminByFilter(
            address, status, title, type, sorting, pageable // title 파라미터 추가
        ))

        // 필터링된 데이터를 가져옴
        val popupPage = popupJpaRepository.findPopupAdminByFilter(
            address, status, title, type, sorting, pageable // title 파라미터 추가
        ).map { popup ->
            modelMapper.map(popup, PopupTO::class.java)
        }

        // 데이터를 매핑하여 반환
        return popupPage
    }

    // POST Popup
    @Transactional
    fun createPopup(
        to: PopupTO,
        thumbnail: MultipartFile,
        images: List<MultipartFile>,
    ) {
        try {
            val mapper = ModelMapper()
            to.status = ActivateStatus.ACTIVE.toString()
            val popup = mapper.map(to, Popup::class.java)

            popup.ageGroup = AgeGroup.fromDisplayName(to.ageGroup!!)

            val savedPlace = popupJpaRepository.save(popup)
            val id = savedPlace.id

            // 썸네일 업로드
            try {
                s3FileService.uploadSingleImage(thumbnail, id, "popup")
            } catch (e: Exception) {
                throw S3Exception("$id : thumbnail 업로드에 실패했습니다.", e)
            }

            // 다중 이미지 업로드
            try {
                s3FileService.uploadMultipleImages(images, id, "popup")
            } catch (e: Exception) {
                throw S3Exception("$id : images 업로드에 실패했습니다.", e)
            }
        } catch (e: DataAccessException) {
            throw RuntimeException("데이터 베이스 등록에 실패했습니다. : ${e.message}", e)
        } catch (e: Exception) {
            throw RuntimeException("오류가 발생했습니다. : ${e.message}", e)
        }
    }

    // PUT Popup : 팝업 상태 변경 -> [ACTIVE, INACTIVE]
    @Transactional
    fun updatePopupStatus(id: Long, status: ActivateStatus) {
        try {
            popupJpaRepository.updateStatusById(id, status)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(e)
        }
    }

    // Delete Popup
    @Transactional
    fun deletePopupById(id: Long) {
        val exists = popupJpaRepository.existsById(id)
        if (!exists) {
            throw ResourceNotFoundException("$id 번호의 팝업 게시글이 존재하지 않습니다.")
        }

        // S3 파일 삭제
        try {
            s3FileService.deleteFiles("popup", id)
        } catch (e: Exception) {
            throw S3Exception("$id 번호의 이미지 파일을 삭제하지 못했습니다.", e)
        }

        // DB에서 팝업 삭제
        popupJpaRepository.deletePopupById(id)
    }

    // Delete Popup Images
    @Transactional
    fun deletePopupImageById(id: Long) {
        try {
            s3FileService.deleteFiles("popup", id)
        } catch (e: Exception) {
            throw S3Exception("$id 번호의 이미지 파일을 삭제하지 못했습니다.", e)
        }
    }

    private fun mappedPopupRespTO(po: Popup): PopupRespTO {
        val popupTO = PopupTO(
            id = po.id,
            customerId = po.customerId,
            type = po.type,
            zipcode = po.zipcode,
            address = po.address,
            addrDetail = po.addrDetail,
            title = po.title,
            description = po.description,
            infra = po.infra,
            startDate = po.startDate,
            endDate = po.endDate,
            ageGroup = po.ageGroup?.desc,
            registeredAt = po.registeredAt,
            status = po.status?.toString()
        )

        val thumbnailFilePath = "popup/${popupTO.id}_thumbnail.png"
        val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

        return PopupRespTO(
            popup = popupTO,
            thumbnail = thumbnailUrl,
        )
    }
}