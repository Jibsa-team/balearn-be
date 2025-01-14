package com.jipsa.balearn.common.dto

import org.springframework.web.multipart.MultipartFile

data class File(
    val contentType: String,
    val bytes: ByteArray,
    val originalFilename: String?,
    val size: Long
) {
    companion object {
        fun from(file: MultipartFile): File {
            val contentType = file.contentType ?: throw IllegalArgumentException("File content type is null")
            return File(contentType, file.bytes, file.originalFilename, file.size)
        }
    }
}