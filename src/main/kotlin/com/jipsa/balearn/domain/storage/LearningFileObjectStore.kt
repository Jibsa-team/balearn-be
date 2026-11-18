package com.jipsa.balearn.domain.storage

import java.io.InputStream

interface LearningFileObjectStore {
    fun openLearningFile(url: String): InputStream?

    // A recognized URL is handled even if its object has already been removed.
    fun deleteLearningFile(url: String): Boolean
}

internal fun learningFileKey(url: String, publicUrl: String): String? {
    val prefix = publicUrl.trimEnd('/') + "/"
    if (!url.startsWith(prefix)) return null
    return url.removePrefix(prefix).takeIf(learningFileObjectKey::matches)
}

private val learningFileObjectKey =
    Regex("learningFile/[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}(?:\\.[a-zA-Z0-9]{0,12})?")
