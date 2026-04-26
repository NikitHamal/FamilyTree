package com.sikai.learn.domain.model

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val subject: String,
    val classLevel: String,
    val topic: String? = null
)

data class QuizAttempt(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val subject: String,
    val classLevel: String,
    val score: Int,
    val total: Int,
    val answers: List<QuizAnswer>
)

data class QuizAnswer(
    val questionId: String,
    val selectedIndex: Int,
    val correct: Boolean,
    val timeSeconds: Int = 0
)

data class WeakTopic(
    val id: String,
    val topic: String,
    val subject: String,
    val incorrectCount: Int,
    val lastAttempted: Long
)
