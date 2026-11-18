package com.jipsa.balearn.infra.storage

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class LocalMediaApi(private val storage: LocalMediaStorage) {
    @GetMapping("/media/{directory}/{name}")
    fun read(@PathVariable directory: String, @PathVariable name: String): ResponseEntity<Resource> {
        if (directory !in setOf("profile", "userProfile", "teamProfile", "teamUserProfile")) {
            return ResponseEntity.notFound().build()
        }
        val contentType = imageTypes[name.substringAfterLast('.')] ?: return ResponseEntity.notFound().build()
        val path = storage.read("$directory/$name") ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok()
            .contentType(contentType)
            .header("X-Content-Type-Options", "nosniff")
            .body(FileSystemResource(path))
    }

    private val imageTypes = mapOf("jpg" to MediaType.IMAGE_JPEG, "png" to MediaType.IMAGE_PNG, "gif" to MediaType.IMAGE_GIF)
}
