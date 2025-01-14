package com.jipsa.balearn.infra.gcs

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.jipsa.balearn.common.dto.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class GcsFileUploader(
    private val storage: Storage,
    @Value("\${spring.cloud.gcp.storage.bucket}")
    private val bucketName: String,
    @Value("\${spring.cloud.gcp.storage.url}")
    private val storagePath: String
) {
    fun uploadImageFile(file: File, directory: String = "profile"): String {
        validateProfileImage(file)

        val fileName = generateFileName(file.originalFilename)
        val blobInfo = BlobInfo.newBuilder(bucketName, "$directory/$fileName")
            .setContentType(file.contentType)
            .build()

        storage.create(blobInfo, file.bytes)

        return "$storagePath$directory/$fileName"
    }

    fun uploadLearningMaterial(file: File, directory: String = "learn"): String {
        validateLearningMaterial(file)

        val fileName = generateFileName(file.originalFilename)
        val blobInfo = BlobInfo.newBuilder(bucketName, "$directory/$fileName")
            .setContentType(file.contentType)
            .build()

        storage.create(blobInfo, file.bytes)

        return "$storagePath$directory/$fileName"
    }

    private fun generateFileName(originalFileName: String?): String {
        val extension = originalFileName?.substringAfterLast('.', "")
        return "${UUID.randomUUID()}.$extension"
    }

    private fun validateProfileImage(file: File) {
        // 파일 크기 검증 (예: 5MB 제한)
        if (file.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("프로필 이미지는 5MB를 초과할 수 없습니다.")
        }

        // 파일 형식 검증
        val allowedContentTypes = listOf("image/jpeg", "image/png", "image/gif")
        if (!allowedContentTypes.contains(file.contentType)) {
            throw IllegalArgumentException("지원하지 않는 이미지 형식입니다. (지원 형식: JPEG, PNG, GIF)")
        }
    }

    private fun validateLearningMaterial(file: File) {
        // 1. 파일 크기 검증 (예: 300MB 제한)
        //    필요에 따라 제한 용량을 조정하세요.
        if (file.size > 300 * 1024 * 1024) {
            throw IllegalArgumentException("파일은 300MB를 초과할 수 없습니다.")
        }

        // 2. 지원 가능한 MIME 타입 목록
        //    문서: PDF, MS 오피스 계열, 텍스트
        //    이미지: JPEG, PNG, GIF
        //    동영상: MP4, MOV, AVI 등
        val allowedContentTypes = listOf(
            // 문서
            "application/pdf",
            "application/msword", // .doc
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
            "application/vnd.ms-powerpoint", // .ppt
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
            "application/vnd.ms-excel", // .xls
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
            "text/plain", // .txt

            // 이미지
            "image/jpeg",
            "image/png",
            "image/gif",

            // 동영상
            "video/mp4",
            "video/x-msvideo",    // .avi
            "video/quicktime",    // .mov
            "video/x-ms-wmv",     // .wmv
            "video/webm"          // .webm
        )

        // 3. 파일 형식(MIME 타입) 검증
        if (!allowedContentTypes.contains(file.contentType)) {
            throw IllegalArgumentException(
                """
            지원하지 않는 파일 형식입니다. 
            (지원 형식: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, TXT, 
              JPEG, PNG, GIF, MP4, AVI, MOV, WMV, WEBM 등)
            """.trimIndent()
            )
        }
    }
}