package com.project.popupmarket.service.aws

import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Object
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.io.IOException
import java.util.function.Consumer
import java.util.stream.Collectors

@Service
@RequiredArgsConstructor
class S3FileService(
    private val s3Client: S3Client,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,

    @Value("\${cloud.aws.region.static}")
    private val region: String
) {
    private val cloudFrontDomain = "https://d3kh2vqqajwnym.cloudfront.net/"

    fun uploadSingleImage(image: MultipartFile, categoryId: Long?, category: String): String {
        val key = "$category/${categoryId}_thumbnail.png"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(image.contentType)
            .build()

        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromInputStream(image.inputStream, image.size)
        )
        return getImageUrl(key)
    }

    fun uploadMultipleImages(images: List<MultipartFile>, categoryId: Long?, category: String): List<String> {
        return images
            .map { image: MultipartFile ->
                try {
                    val imageIndex = images.indexOf(image) + 1
                    val key = "$category/${categoryId}_images_${imageIndex}.png"

                    val putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(image.contentType)
                        .build()

                    s3Client.putObject(
                        putObjectRequest,
                        RequestBody.fromInputStream(image.inputStream, image.size)
                    )
                    return@map getImageUrl(key)
                } catch (e: IOException) {
                    throw RuntimeException("Image upload failed: " + image.originalFilename, e)
                }
            }
    }

    fun deleteFiles(category: String?, categoryId: Long?): List<String> {
        val prefix = "$category/${categoryId}_"

        val listRequest = ListObjectsV2Request.builder()
            .bucket(bucket)
            .prefix(prefix)
            .build()

        val listResponse = s3Client.listObjectsV2(listRequest)

        val keysToDelete = listResponse.contents().map { it.key() }

        // S3에서 각 파일 삭제
        keysToDelete.forEach(Consumer { key: String ->
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
            s3Client.deleteObject(deleteRequest)
        })

        return keysToDelete
    }

//    private val cloudFrontDomain = "https://d3kh2vqqajwnym.cloudfront.net/"
    fun getCloudFrontImageUrl(filePath: String): String {
//        "/land/0_0_thumbnail_test.png"
        return "$cloudFrontDomain$filePath"
    }

    fun getCloudFrontImageListUrl(filePath: String): List<String> {
//        "/land/0_0_images_0.png"

        val request = ListObjectsV2Request.builder()
            .bucket(bucket)
            .prefix(filePath)
            .build()

        val response = s3Client.listObjectsV2(request)

        val imageUrls: MutableList<String> = ArrayList()
        for (s3Object in response.contents()) {
            imageUrls.add(cloudFrontDomain + s3Object.key())
        }

        return imageUrls
    }

    private fun getImageUrl(fileName: String): String {
        return "https://$bucket.s3.$region.amazonaws.com/$fileName"
    }
}
