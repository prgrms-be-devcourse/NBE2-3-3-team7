package com.project.popupmarket.service.land

import com.project.popupmarket.dto.land.RentalLandRespTO
import com.project.popupmarket.dto.land.RentalLandTO
import com.project.popupmarket.entity.RentalLand
import com.project.popupmarket.enums.ActivateStatus
import com.project.popupmarket.exception.custom.ResourceNotFoundException
import com.project.popupmarket.exception.custom.S3Exception
import com.project.popupmarket.repository.RentalLandJpaRepository
import com.project.popupmarket.service.aws.S3FileService
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDate

@Service
class RentalLandService(
    private val rentalLandJpaRepository: RentalLandJpaRepository,
    private val s3FileService: S3FileService,
) {

    fun findById(id: Long): RentalLandTO {
        return rentalLandJpaRepository.findById(id)
            .map { rentalLand ->
                ModelMapper().map(rentalLand, RentalLandTO::class.java)
            }
            .orElseThrow { ResourceNotFoundException("${id}번 의 임대지 게시글이 없습니다.") }
    }

    // 2 - 1. Read : 조건에 해당하는 팝업들 미리보기
    fun findByFilter(
        minArea: Int?, maxArea: Int?, location: String?,
        minPrice: BigDecimal?, maxPrice: BigDecimal?,
        startDate: LocalDate?, endDate: LocalDate?,
        sorting: String?, pageable: Pageable?,
    ): Page<RentalLandRespTO> {
        val modelMapper = ModelMapper()
        val rentalLandRespTO = rentalLandJpaRepository
            .findByFilter(
                minArea, maxArea, location,
                minPrice, maxPrice,
                startDate, endDate, sorting, pageable
            )
            .map { rp: RentalLand ->
                val rentalLandTO = modelMapper.map(rp, RentalLandTO::class.java)
                val thumbnailFilePath = "land/${rentalLandTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val respTO = RentalLandRespTO()
                respTO.rentalLand = rentalLandTO
                respTO.thumbnail = thumbnailUrl
                respTO
            }

        return rentalLandRespTO
    }

    // 2 - 2. Read : 특정 번호에 해당하는 임대지 상세 정보
    fun findRentalLandById(id: Long): RentalLandRespTO {
        return rentalLandJpaRepository.findById(id)
            .map<RentalLandRespTO> { rentalLand: RentalLand ->
                val rentalLandTO = ModelMapper().map(rentalLand, RentalLandTO::class.java)

                val thumbnailFilePath = "land/${rentalLandTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val filePath = "land/${rentalLandTO.id}_images_"
                val imageUrls = s3FileService.getCloudFrontImageListUrl(filePath)

                val respTO = RentalLandRespTO()
                respTO.rentalLand = rentalLandTO
                respTO.thumbnail = thumbnailUrl
                respTO.images = imageUrls
                respTO
            }
            .orElseThrow { ResourceNotFoundException("${id}번 의 임대지 게시글이 없습니다.") }
    }

    //  2 - 3. Read : 관리 중인 임대지 목록
    fun findByUserId(userSeq: Long): List<RentalLandRespTO> {
        val modelMapper = ModelMapper()

        val rentalLandRespTO = rentalLandJpaRepository.findByUserId(userSeq)
            .map { rp ->
                val rentalLandTO = modelMapper.map(rp, RentalLandTO::class.java)

                val thumbnailFilePath = "land/${rentalLandTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                val respTO = RentalLandRespTO()
                respTO.rentalLand = rentalLandTO
                respTO.thumbnail = thumbnailUrl
                respTO
            }

        if (rentalLandRespTO.isEmpty()) {
            // 데이터가 없을 경우 예외 발생
            throw ResourceNotFoundException("사용자의 임대지 리스트가 없습니다.")
        }
        return rentalLandRespTO
    }

    // 2 - 4. Read : 메인 페이지 임대지 10개 조회
    fun findByLimit(): List<RentalLandRespTO> {
        val modelMapper = ModelMapper()
        val rentalLandRespTO = rentalLandJpaRepository.findByLimit()
            .map { rp: RentalLand ->
                val rentalLandTO = modelMapper.map(rp, RentalLandTO::class.java)
                val thumbnailFilePath = "land/${rentalLandTO.id}_thumbnail.png"
                val thumbnailUrl = s3FileService.getCloudFrontImageUrl(thumbnailFilePath)

                // RentalLandRespTO 생성 및 데이터 설정
                val respTO = RentalLandRespTO()
                respTO.rentalLand = rentalLandTO
                respTO.thumbnail = thumbnailUrl
                respTO
            }

        if (rentalLandRespTO.isEmpty()) {
            // 데이터가 없을 경우 예외 발생
            throw ResourceNotFoundException("임대지에 대한 데이터가 없습니다.")
        }

        return rentalLandRespTO
    }

    // 2 - 5. Read : 관리자 페이지 조건에 해당하는 임대지 20개 조회 + 검색 포함
    // TODO : 추후 Admin Migration 완료 이후에 'AdminService' 로 이동
    fun findLandAdminByFilter(
        address: String?, status: ActivateStatus?,
        title: String?, // title 추가
        sorting: String?, pageable: Pageable?,
    ): Page<RentalLandTO> {
        val modelMapper = ModelMapper()

        // 필터링된 데이터를 가져옴
        val rentalLandPage = rentalLandJpaRepository.findLandAdminByFilter(
            address, status, title, sorting, pageable // title 파라미터 추가
        ).map { rentalLand ->
            modelMapper.map(rentalLand, RentalLandTO::class.java)
        }

        // 데이터를 매핑하여 반환
        return rentalLandPage
    }

    // 1. Create : 임대지 추가
    @Transactional
    fun insert(
        to: RentalLandTO,
        thumbnail: MultipartFile,
        images: List<MultipartFile>,
    ) {
        try {
            val mapper = ModelMapper()
            to.status = ActivateStatus.ACTIVE.toString()
            val rentalLand = mapper.map(to, RentalLand::class.java)

            val savedPlace = rentalLandJpaRepository.save(rentalLand)
            val id = savedPlace.id

            // 썸네일 업로드
            try {
                s3FileService.uploadSingleImage(thumbnail, id, "land")
            } catch (e: Exception) {
                throw S3Exception("$id : thumbnail 업로드에 실패했습니다.", e)
            }

            // 다중 이미지 업로드
            try {
                s3FileService.uploadMultipleImages(images, id, "land")
            } catch (e: Exception) {
                throw S3Exception("$id : images 업로드에 실패했습니다.", e)
            }
        } catch (e: DataAccessException) {
            throw RuntimeException("데이터 베이스 등록에 실패했습니다. : ${e.message}", e)
        } catch (e: Exception) {
            throw RuntimeException("오류가 발생했습니다. : ${e.message}", e)
        }
    }

    // 3. Update : 임대지 상태 변경 -> [ACTIVE, INACTIVE]
    @Transactional
    fun updateStatus(id: Long, status: String) {
        try {
            val changedStatus = ActivateStatus.valueOf(status)

            rentalLandJpaRepository.updateStatusById(id, changedStatus.name)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(e)
        }
    }

    // 4 - 1 . Delete : 임대지 이미지 삭제
    @Transactional
    fun delete(id: Long) {
        // DB에서 삭제 대상 확인

        val exists = rentalLandJpaRepository.existsById(id)
        if (!exists) {
            throw ResourceNotFoundException("$id 번호의 임대지 게시글이 존재하지 않습니다.")
        }

        // S3 파일 삭제
        try {
            s3FileService.deleteFiles("land", id)
        } catch (e: Exception) {
            throw S3Exception("$id 번호의 이미지 파일을 삭제하지 못했습니다.", e)
        }

        // DB에서 임대지 삭제
        rentalLandJpaRepository.deleteRentalLandById(id)
    }

    // 4 - 2. Delete : 임대지 이미지 삭제
    @Transactional
    fun deleteImage(id: Long) {
        try {
            s3FileService.deleteFiles("land", id)
        } catch (e: Exception) {
            throw S3Exception("$id 번호의 이미지 파일을 삭제하지 못했습니다.", e)
        }
    }
}


