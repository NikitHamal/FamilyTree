package com.sikai.learn.domain.model

enum class ContentType {
    TEXTBOOK, PAST_PAPER, MCQ_PACK, SYLLABUS, NOTES
}

data class ContentManifestItem(
    val id: String,
    val title: String,
    val type: ContentType,
    val classLevel: String,
    val subject: String,
    val year: String? = null,
    val fileUrl: String? = null,
    val fileKey: String? = null,
    val sizeBytes: Long = 0L,
    val checksumSha256: String? = null,
    val version: String = "1",
    val updatedAt: Long = System.currentTimeMillis(),
    val language: String = "en",
    val tags: List<String> = emptyList()
)

data class DownloadedFile(
    val id: String,
    val localPath: String,
    val manifestItemId: String,
    val downloadedAt: Long,
    val verified: Boolean = false
)
