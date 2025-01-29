package com.project.popupmarket.service.popup

import com.project.popupmarket.dto.popup.PopupRespTO
import com.project.popupmarket.dto.popup.PopupTO
import com.project.popupmarket.entity.Popup
import com.project.popupmarket.enums.ActivateStatus
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
open class PopupService(
    private val popupJpaRepository: PopupJpaRepository,
    private val s3FileService: S3FileService
) {
    // 해당 번호 팝업 찾아 TO로 반환
    fun findById(id: Long): PopupTO {
        return popupJpaRepository.findById(id)
            .map { popup ->
                ModelMapper().map(popup, PopupTO::class.java)
            }
            .orElseThrow{ ResourceNotFoundException("$id 의 팝업 게시글이 없습니다.") }
    }

    // 1. GET Popup By Id : 임대지 상세 정보
    fun getUserWithImages(id: Long): PopupRespTO {
        return popupJpaRepository.findById(id)
            .map<PopupRespTO> { popup: Popup ->
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
            .orElseThrow { ResourceNotFoundException("$id 의 팝업 게시글이 없습니다.")}
    }

    // 2. GET Filtered Popup
    fun findFilterWithPagination(
        targetLocation: String?, type: String?, targetAgeGroup: String?,
        startDate: LocalDate?, endDate: LocalDate?, sorting: String?,
        pageable: Pageable?
    ) : Page<PopupRespTO> {
        val modelMapper = ModelMapper()
        val popupRespTO = popupJpaRepository
            .findFilteredWithPagination(
                targetLocation, type, targetAgeGroup, startDate, endDate,
                sorting, pageable
            )
            .map<PopupRespTO> { po: Popup ->
                val popupTO = modelMapper.map(po, PopupTO::class.java)
                val thumbnailFilePath = "popup/${popupTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val respTO = PopupRespTO()
                respTO.popup = popupTO
                respTO.thumbnail = thumbnailUrl
                respTO
            }
        if (popupRespTO.isEmpty) {
            throw ResourceNotFoundException("조건에 해당하는 팝업이 없습니다.")
        }
        return popupRespTO
    }

    // 3. GET User's Popup
    fun findPopupByUserId(userSeq: Long): List<PopupRespTO> {
        val modelMapper = ModelMapper()

        val popupRespTO = popupJpaRepository.findPopupByUserId(userSeq)
            .map{ po ->
                val popupTO = modelMapper.map(po, PopupTO::class.java)

                val thumbnailFilePath = "popup/${popupTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val respTO = PopupRespTO()
                respTO.popup = popupTO
                respTO.thumbnail = thumbnailUrl
                respTO
            }
        if (popupRespTO.isEmpty()) {
            throw ResourceNotFoundException("사용자의 팝업 리스트가 없습니다.")
        }
        return popupRespTO
    }

    // 4. GET Main Popup
    fun findWithLimit(): List<PopupRespTO> {
        val modelMapper = ModelMapper()
        val popupRespTO = popupJpaRepository.findWithLimit()
            .map{ po ->
                val popupTO = modelMapper.map(po, PopupTO::class.java)

                val thumbnailFilePath = "popup/${popupTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val respTO = PopupRespTO()
                respTO.popup = popupTO
                respTO.thumbnail = thumbnailUrl
                respTO
            }
        if (popupRespTO.isEmpty()) {
            throw ResourceNotFoundException("팝업에 대한 데이터가 없습니다.")
        }
        return popupRespTO
    }

    // POST Popup
    @Transactional
    open fun createPopup(
        to: PopupTO,
        thumbnail: MultipartFile,
        images: List<MultipartFile>
    ) {
        try {
            val mapper = ModelMapper()
            to.status = ActivateStatus.ACTIVE.toString()
            val popup = mapper.map(to, Popup::class.java)

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
    open fun updatePopupStatus(id: Long, status: String) {
        try {
            val changedStatus = ActivateStatus.valueOf(status)

            popupJpaRepository.updateStatusById(id, changedStatus.name)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(e)
        }
    }

    // Delete Popup
    @Transactional
    open fun deletePopupById(id: Long) {
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
    open fun deletePopupImageById(id: Long) {
        try {
            s3FileService.deleteFiles("popup", id)
        } catch (e: Exception) {
            throw S3Exception("$id 번호의 이미지 파일을 삭제하지 못했습니다.", e)
        }
    }
}