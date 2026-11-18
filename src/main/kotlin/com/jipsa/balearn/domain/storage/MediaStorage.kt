package com.jipsa.balearn.domain.storage

import com.jipsa.balearn.common.dto.File

fun interface MediaStorage {
    fun put(key: String, file: File): String
}
