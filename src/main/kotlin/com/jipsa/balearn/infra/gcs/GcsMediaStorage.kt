package com.jipsa.balearn.infra.gcs

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.Storage
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.storage.MediaStorage
import com.jipsa.balearn.domain.storage.LearningFileObjectStore
import com.jipsa.balearn.domain.storage.learningFileKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.channels.Channels

@Component
@ConditionalOnProperty(name = ["balearn.storage.provider"], havingValue = "gcs")
class GcsMediaStorage(
    private val storage: Storage,
    @Value("\${spring.cloud.gcp.storage.bucket}") private val bucket: String,
    @Value("\${spring.cloud.gcp.storage.url}") private val publicUrl: String,
    @Value("\${balearn.storage.gcs-learning-bucket}") private val learningBucket: String
) : MediaStorage, LearningFileObjectStore {
    init {
        require(bucket.isNotBlank() && publicUrl.startsWith("https://") && publicUrl.removePrefix("https://").trimEnd('/').isNotBlank()) {
            "GCS storage requires BUCKET_NAME and a public HTTPS media URL"
        }
        require(learningBucket.isBlank() || learningBucket != bucket) { "Use a separate private GCS bucket for learning files" }
    }

    override fun put(key: String, file: File): String {
        val learningFile = key.startsWith("learningFile/")
        if (learningFile) check(learningBucket.isNotBlank()) { "Set GCS_LEARNING_BUCKET to a private bucket" }
        val blob = BlobInfo.newBuilder(if (learningFile) learningBucket else bucket, key).setContentType(file.contentType)
        if (learningFile) blob.setCacheControl("no-store")
        storage.create(blob.build(), file.bytes)
        return if (learningFile) "gs://$learningBucket/$key" else "${publicUrl.trimEnd('/')}/$key"
    }

    override fun openLearningFile(url: String): InputStream? {
        val id = learningObject(url) ?: return null
        if (storage.get(id) == null) return null
        return Channels.newInputStream(storage.reader(id))
    }

    override fun deleteLearningFile(url: String): Boolean {
        val id = learningObject(url) ?: return false
        storage.delete(id)
        return true
    }

    private fun learningObject(url: String): BlobId? {
        if (learningBucket.isNotBlank()) {
            learningFileKey(url, "gs://$learningBucket")?.let { return BlobId.of(learningBucket, it) }
        }
        return learningFileKey(url, publicUrl)?.let { BlobId.of(bucket, it) }
    }
}
