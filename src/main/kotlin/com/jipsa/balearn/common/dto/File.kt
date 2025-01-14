package com.jipsa.balearn.common.dto

data class File(
    val contentType: String,
    val bytes: ByteArray,
    val originalFilename: String?,
    val size: Long
)