package com.famy.tree.data.local

import android.content.Context
import com.famy.tree.data.model.AppSettings
import com.famy.tree.data.model.CustomField
import com.famy.tree.data.model.EventType
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.FamilyTree
import com.famy.tree.data.model.Gender
import com.famy.tree.data.model.LifeEvent
import com.famy.tree.data.model.MediaItem
import com.famy.tree.data.model.Relationship
import com.famy.tree.data.model.RelationshipType
import com.famy.tree.data.model.ThemePreference
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LocalFamilyRepository(context: Context) : FamilyRepository {
    private val file = File(context.filesDir, "famy_state.json")

    override suspend fun load(): FamilyState = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            val initial = FamilyState()
            saveBlocking(initial)
            return@withContext initial
        }
        runCatching { parseState(file.readText()) }.getOrElse {
            val recovered = FamilyState()
            saveBlocking(recovered)
            recovered
        }
    }

    override suspend fun save(state: FamilyState) = withContext(Dispatchers.IO) {
        saveBlocking(state)
    }

    override suspend fun exportJson(): String = withContext(Dispatchers.IO) {
        toJson(load()).toString(2)
    }

    override suspend fun importJson(json: String): FamilyState = withContext(Dispatchers.IO) {
        val imported = parseState(json)
        saveBlocking(imported)
        imported
    }

    override suspend fun replace(state: FamilyState) = withContext(Dispatchers.IO) {
        saveBlocking(state)
    }

    override suspend fun clear(): FamilyState = withContext(Dispatchers.IO) {
        val blank = FamilyState()
        saveBlocking(blank)
        blank
    }

    private fun saveBlocking(state: FamilyState) {
        val parent = file.parentFile
        if (parent != null && !parent.exists()) parent.mkdirs()
        val tmp = File(file.parentFile, "${file.name}.tmp")
        tmp.writeText(toJson(state).toString(2))
        if (!tmp.renameTo(file)) {
            file.writeText(tmp.readText())
            tmp.delete()
        }
    }

    private fun parseState(raw: String): FamilyState {
        val json = JSONObject(raw)
        return FamilyState(
            schemaVersion = json.optInt("schemaVersion", 1),
            tree = parseTree(json.optJSONObject("tree") ?: JSONObject()),
            members = json.optJSONArray("members").toList { parseMember(it as JSONObject) },
            relationships = json.optJSONArray("relationships").toList { parseRelationship(it as JSONObject) },
            media = json.optJSONArray("media").toList { parseMedia(it as JSONObject) },
            settings = parseSettings(json.optJSONObject("settings") ?: JSONObject())
        )
    }

    private fun parseTree(json: JSONObject): FamilyTree = FamilyTree(
        id = json.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
        name = json.optString("name", "My Family Tree"),
        description = json.optString("description", ""),
        rootMemberId = json.optNullableString("rootMemberId"),
        createdAt = json.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = json.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun parseMember(json: JSONObject): FamilyMember = FamilyMember(
        id = json.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
        displayName = json.optString("displayName", "Unnamed person"),
        givenName = json.optString("givenName", ""),
        familyName = json.optString("familyName", ""),
        gender = enumValueOrDefault(json.optString("gender"), Gender.UNKNOWN),
        birthDate = json.optString("birthDate", ""),
        birthPlace = json.optString("birthPlace", ""),
        deathDate = json.optString("deathDate", ""),
        deathPlace = json.optString("deathPlace", ""),
        marriageDate = json.optString("marriageDate", ""),
        marriagePlace = json.optString("marriagePlace", ""),
        notes = json.optString("notes", ""),
        photoUri = json.optString("photoUri", ""),
        customFields = json.optJSONArray("customFields").toList { item ->
            val field = item as JSONObject
            CustomField(
                id = field.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
                label = field.optString("label", ""),
                value = field.optString("value", "")
            )
        },
        events = json.optJSONArray("events").toList { item ->
            val event = item as JSONObject
            LifeEvent(
                id = event.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
                type = enumValueOrDefault(event.optString("type"), EventType.CUSTOM),
                title = event.optString("title", "Event"),
                date = event.optString("date", ""),
                place = event.optString("place", ""),
                notes = event.optString("notes", "")
            )
        },
        createdAt = json.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = json.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun parseRelationship(json: JSONObject): Relationship = Relationship(
        id = json.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
        sourceMemberId = json.optString("sourceMemberId", ""),
        targetMemberId = json.optString("targetMemberId", ""),
        type = enumValueOrDefault(json.optString("type"), RelationshipType.PARENT_CHILD),
        startDate = json.optString("startDate", ""),
        endDate = json.optString("endDate", ""),
        notes = json.optString("notes", "")
    )

    private fun parseMedia(json: JSONObject): MediaItem = MediaItem(
        id = json.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
        memberId = json.optNullableString("memberId"),
        title = json.optString("title", "Media"),
        uri = json.optString("uri", ""),
        type = json.optString("type", "photo"),
        notes = json.optString("notes", "")
    )

    private fun parseSettings(json: JSONObject): AppSettings = AppSettings(
        themePreference = enumValueOrDefault(json.optString("themePreference"), ThemePreference.SYSTEM),
        dateFormat = json.optString("dateFormat", "MMM d, yyyy"),
        compactTreeCards = json.optBoolean("compactTreeCards", true),
        showDeceasedBadges = json.optBoolean("showDeceasedBadges", true),
        onboardingComplete = json.optBoolean("onboardingComplete", false)
    )

    private fun toJson(state: FamilyState): JSONObject = JSONObject()
        .put("schemaVersion", state.schemaVersion)
        .put("tree", JSONObject()
            .put("id", state.tree.id)
            .put("name", state.tree.name)
            .put("description", state.tree.description)
            .put("rootMemberId", state.tree.rootMemberId)
            .put("createdAt", state.tree.createdAt)
            .put("updatedAt", state.tree.updatedAt))
        .put("members", JSONArray().apply { state.members.forEach { put(memberToJson(it)) } })
        .put("relationships", JSONArray().apply { state.relationships.forEach { put(relationshipToJson(it)) } })
        .put("media", JSONArray().apply { state.media.forEach { put(mediaToJson(it)) } })
        .put("settings", JSONObject()
            .put("themePreference", state.settings.themePreference.name)
            .put("dateFormat", state.settings.dateFormat)
            .put("compactTreeCards", state.settings.compactTreeCards)
            .put("showDeceasedBadges", state.settings.showDeceasedBadges)
            .put("onboardingComplete", state.settings.onboardingComplete))

    private fun memberToJson(member: FamilyMember): JSONObject = JSONObject()
        .put("id", member.id)
        .put("displayName", member.displayName)
        .put("givenName", member.givenName)
        .put("familyName", member.familyName)
        .put("gender", member.gender.name)
        .put("birthDate", member.birthDate)
        .put("birthPlace", member.birthPlace)
        .put("deathDate", member.deathDate)
        .put("deathPlace", member.deathPlace)
        .put("marriageDate", member.marriageDate)
        .put("marriagePlace", member.marriagePlace)
        .put("notes", member.notes)
        .put("photoUri", member.photoUri)
        .put("createdAt", member.createdAt)
        .put("updatedAt", member.updatedAt)
        .put("customFields", JSONArray().apply {
            member.customFields.forEach { put(JSONObject().put("id", it.id).put("label", it.label).put("value", it.value)) }
        })
        .put("events", JSONArray().apply {
            member.events.forEach { put(JSONObject().put("id", it.id).put("type", it.type.name).put("title", it.title).put("date", it.date).put("place", it.place).put("notes", it.notes)) }
        })

    private fun relationshipToJson(relationship: Relationship): JSONObject = JSONObject()
        .put("id", relationship.id)
        .put("sourceMemberId", relationship.sourceMemberId)
        .put("targetMemberId", relationship.targetMemberId)
        .put("type", relationship.type.name)
        .put("startDate", relationship.startDate)
        .put("endDate", relationship.endDate)
        .put("notes", relationship.notes)

    private fun mediaToJson(media: MediaItem): JSONObject = JSONObject()
        .put("id", media.id)
        .put("memberId", media.memberId)
        .put("title", media.title)
        .put("uri", media.uri)
        .put("type", media.type)
        .put("notes", media.notes)
}

private inline fun <reified T : Enum<T>> enumValueOrDefault(name: String?, default: T): T {
    return runCatching { enumValueOf<T>(name.orEmpty()) }.getOrDefault(default)
}

private fun JSONObject.optNullableString(key: String): String? = if (has(key) && !isNull(key)) optString(key) else null

private inline fun <T> JSONArray?.toList(transform: (Any) -> T): List<T> {
    if (this == null) return emptyList()
    val result = ArrayList<T>(length())
    for (index in 0 until length()) result.add(transform(get(index)))
    return result
}
