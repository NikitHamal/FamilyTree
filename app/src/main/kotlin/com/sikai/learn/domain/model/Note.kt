package com.sikai.learn.domain.model

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val aiSummary: String? = null
)

data class SavedAiAnswer(
    val id: String,
    val question: String,
    val answer: String,
    val providerId: String,
    val subject: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
