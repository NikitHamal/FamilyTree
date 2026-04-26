package com.famy.tree.data.model

import java.util.UUID

enum class Gender { FEMALE, MALE, NON_BINARY, UNKNOWN }
enum class RelationshipType { PARENT_CHILD, SPOUSE, SIBLING }
enum class EventType { BIRTH, DEATH, MARRIAGE, RESIDENCE, EDUCATION, WORK, CUSTOM }
enum class ThemePreference { SYSTEM, LIGHT, DARK }

data class CustomField(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val value: String
)

data class LifeEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: EventType,
    val title: String,
    val date: String = "",
    val place: String = "",
    val notes: String = ""
)

data class MediaItem(
    val id: String = UUID.randomUUID().toString(),
    val memberId: String? = null,
    val title: String,
    val uri: String,
    val type: String = "photo",
    val notes: String = ""
)

data class FamilyMember(
    val id: String = UUID.randomUUID().toString(),
    val displayName: String,
    val givenName: String = "",
    val familyName: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val birthDate: String = "",
    val birthPlace: String = "",
    val deathDate: String = "",
    val deathPlace: String = "",
    val marriageDate: String = "",
    val marriagePlace: String = "",
    val notes: String = "",
    val photoUri: String = "",
    val customFields: List<CustomField> = emptyList(),
    val events: List<LifeEvent> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isLiving: Boolean get() = deathDate.isBlank()
    val initials: String get() = displayName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercaseChar().toString() }.ifBlank { "?" }
}

data class Relationship(
    val id: String = UUID.randomUUID().toString(),
    val sourceMemberId: String,
    val targetMemberId: String,
    val type: RelationshipType,
    val startDate: String = "",
    val endDate: String = "",
    val notes: String = ""
)

data class FamilyTree(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My Family Tree",
    val description: String = "",
    val rootMemberId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AppSettings(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val dateFormat: String = "MMM d, yyyy",
    val compactTreeCards: Boolean = true,
    val showDeceasedBadges: Boolean = true,
    val onboardingComplete: Boolean = false
)

data class FamilyState(
    val tree: FamilyTree = FamilyTree(),
    val members: List<FamilyMember> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val media: List<MediaItem> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val schemaVersion: Int = 1
)
