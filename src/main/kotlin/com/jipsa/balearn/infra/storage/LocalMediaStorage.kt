package com.jipsa.balearn.infra.storage

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.storage.MediaStorage
import com.jipsa.balearn.domain.storage.LearningFileObjectStore
import com.jipsa.balearn.domain.storage.learningFileKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Component
class LocalMediaStorage(
    @Value("\${balearn.storage.local-directory}") directory: String,
    @Value("\${balearn.storage.public-url}") private val publicUrl: String
) : LearningFileObjectStore {
    private val directory = Path.of(directory).toAbsolutePath().normalize()

    fun put(key: String, file: File): String {
        val root = Files.createDirectories(directory).toRealPath()
        val target = requireNotNull(resolve(root, key))
        Files.createDirectories(target.parent)
        require(target.parent.toRealPath().startsWith(root)) { "Invalid media directory" }
        val temp = Files.createTempFile(target.parent, ".upload-", ".tmp")
        try {
            Files.write(temp, file.bytes)
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE)
        } finally {
            Files.deleteIfExists(temp)
        }
        return "${publicUrl.trimEnd('/')}/$key"
    }

    fun read(key: String): Path? {
        if (!Files.isDirectory(directory)) return null
        val root = directory.toRealPath()
        val target = resolve(root, key) ?: return null
        if (!Files.isRegularFile(target)) return null
        return target.toRealPath().takeIf { it.startsWith(root) }
    }

    override fun openLearningFile(url: String): InputStream? {
        val key = learningFileKey(url, publicUrl) ?: return null
        return read(key)?.let { Files.newInputStream(it) }
    }

    override fun deleteLearningFile(url: String): Boolean {
        val key = learningFileKey(url, publicUrl) ?: return false
        if (Files.notExists(directory)) return true
        val root = directory.toRealPath()
        val target = root.resolve(key)
        if (Files.notExists(target.parent)) return true
        require(target.parent.toRealPath().startsWith(root)) { "Invalid media directory" }
        Files.deleteIfExists(target)
        return true
    }

    private fun resolve(root: Path, key: String): Path? {
        if (!mediaKey.matches(key)) return null
        return root.resolve(key)
    }

    private companion object {
        val mediaKey = Regex("[a-zA-Z][a-zA-Z0-9]*/[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}(?:\\.[a-zA-Z0-9]{1,12})?")
    }
}

@Configuration
class LocalMediaConfig {
    @Bean
    @ConditionalOnProperty(name = ["balearn.storage.provider"], havingValue = "local", matchIfMissing = true)
    fun localMediaWriter(storage: LocalMediaStorage): MediaStorage = MediaStorage(storage::put)
}
