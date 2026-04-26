package com.sikai.learn.domain.model

data class StudyPlan(
    val id: String,
    val title: String,
    val examDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class StudyTask(
    val id: String,
    val planId: String,
    val title: String,
    val subject: String,
    val scheduledDate: Long,
    val completed: Boolean = false,
    val completedAt: Long? = null
)
