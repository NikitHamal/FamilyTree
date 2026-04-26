package com.sikai.learn.domain.model

data class PastPaper(
    val id: String,
    val title: String,
    val classLevel: String,
    val subject: String,
    val year: String,
    val downloaded: Boolean = false,
    val localPath: String? = null
)
