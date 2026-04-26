package com.sikai.learn.domain.model

enum class ClassLevel(val displayName: String) {
    CLASS_8("Class 8"),
    CLASS_10("Class 10"),
    CLASS_12("Class 12")
}

enum class Subject(val displayName: String, val applicableClasses: List<ClassLevel>) {
    ENGLISH("English", ClassLevel.entries),
    NEPALI("Nepali", ClassLevel.entries),
    MATHEMATICS("Mathematics", ClassLevel.entries),
    SCIENCE("Science", listOf(ClassLevel.CLASS_8, ClassLevel.CLASS_10)),
    SOCIAL_STUDIES("Social Studies", listOf(ClassLevel.CLASS_8, ClassLevel.CLASS_10)),
    PHYSICS("Physics", listOf(ClassLevel.CLASS_12)),
    CHEMISTRY("Chemistry", listOf(ClassLevel.CLASS_12)),
    BIOLOGY("Biology", listOf(ClassLevel.CLASS_12)),
    ACCOUNT("Account", listOf(ClassLevel.CLASS_12)),
    ECONOMICS("Economics", listOf(ClassLevel.CLASS_12)),
    COMPUTER_SCIENCE("Computer Science", ClassLevel.entries)
}
