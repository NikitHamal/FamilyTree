package com.sikai.learn.domain.model

data class UserProfile(
    val id: String = "default",
    val classLevel: ClassLevel = ClassLevel.CLASS_10,
    val subjects: List<Subject> = emptyList(),
    val language: String = "en",
    val examDate: Long? = null,
    val dailyMinutes: Int = 60,
    val onboardingComplete: Boolean = false,
    val streakDays: Int = 0,
    val xpPoints: Int = 0,
    val lastStudyDate: Long? = null
)
