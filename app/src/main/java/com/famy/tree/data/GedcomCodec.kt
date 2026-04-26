package com.famy.tree.data

import com.famy.tree.data.model.EventType
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.Gender
import com.famy.tree.data.model.LifeEvent
import com.famy.tree.data.model.Relationship
import com.famy.tree.data.model.RelationshipType

object GedcomCodec {
    fun export(state: FamilyState): String {
        val builder = StringBuilder()
        builder.appendLine("0 HEAD")
        builder.appendLine("1 SOUR Famy")
        builder.appendLine("1 GEDC")
        builder.appendLine("2 VERS 5.5.1")
        builder.appendLine("1 CHAR UTF-8")
        state.members.forEach { member ->
            builder.appendLine("0 @I${member.id.safeGedcomId()}@ INDI")
            builder.appendLine("1 NAME ${member.displayName.escapeGedcom()}")
            builder.appendLine("1 SEX ${member.gender.toGedcomSex()}")
            if (member.birthDate.isNotBlank() || member.birthPlace.isNotBlank()) {
                builder.appendLine("1 BIRT")
                if (member.birthDate.isNotBlank()) builder.appendLine("2 DATE ${member.birthDate.escapeGedcom()}")
                if (member.birthPlace.isNotBlank()) builder.appendLine("2 PLAC ${member.birthPlace.escapeGedcom()}")
            }
            if (member.deathDate.isNotBlank() || member.deathPlace.isNotBlank()) {
                builder.appendLine("1 DEAT")
                if (member.deathDate.isNotBlank()) builder.appendLine("2 DATE ${member.deathDate.escapeGedcom()}")
                if (member.deathPlace.isNotBlank()) builder.appendLine("2 PLAC ${member.deathPlace.escapeGedcom()}")
            }
            if (member.notes.isNotBlank()) builder.appendLine("1 NOTE ${member.notes.escapeGedcom()}")
        }
        state.relationships.filter { it.type == RelationshipType.SPOUSE }.forEachIndexed { index, relationship ->
            builder.appendLine("0 @F$index@ FAM")
            builder.appendLine("1 HUSB @I${relationship.sourceMemberId.safeGedcomId()}@")
            builder.appendLine("1 WIFE @I${relationship.targetMemberId.safeGedcomId()}@")
        }
        state.relationships.filter { it.type == RelationshipType.PARENT_CHILD }.forEachIndexed { index, relationship ->
            builder.appendLine("0 @PC$index@ FAM")
            builder.appendLine("1 HUSB @I${relationship.sourceMemberId.safeGedcomId()}@")
            builder.appendLine("1 CHIL @I${relationship.targetMemberId.safeGedcomId()}@")
        }
        builder.appendLine("0 TRLR")
        return builder.toString()
    }

    fun import(raw: String, current: FamilyState = FamilyState()): FamilyState {
        val members = mutableListOf<FamilyMember>()
        var activeId = ""
        var activeName = ""
        var activeGender = Gender.UNKNOWN
        var birthDate = ""
        var birthPlace = ""
        var deathDate = ""
        var deathPlace = ""
        var note = ""
        var lastEvent: String? = null

        fun flush() {
            if (activeId.isNotBlank() || activeName.isNotBlank()) {
                val name = activeName.ifBlank { "Imported person ${members.size + 1}" }
                members.add(
                    FamilyMember(
                        id = activeId.ifBlank { java.util.UUID.randomUUID().toString() },
                        displayName = name,
                        gender = activeGender,
                        birthDate = birthDate,
                        birthPlace = birthPlace,
                        deathDate = deathDate,
                        deathPlace = deathPlace,
                        notes = note,
                        events = listOfNotNull(
                            birthDate.takeIf { it.isNotBlank() }?.let { LifeEvent(type = EventType.BIRTH, title = "Birth", date = it, place = birthPlace) },
                            deathDate.takeIf { it.isNotBlank() }?.let { LifeEvent(type = EventType.DEATH, title = "Death", date = it, place = deathPlace) }
                        )
                    )
                )
            }
            activeId = ""
            activeName = ""
            activeGender = Gender.UNKNOWN
            birthDate = ""
            birthPlace = ""
            deathDate = ""
            deathPlace = ""
            note = ""
            lastEvent = null
        }

        raw.lineSequence().forEach { line ->
            val clean = line.trim()
            if (clean.startsWith("0 @") && clean.endsWith(" INDI")) {
                flush()
                activeId = clean.substringAfter("@").substringBefore("@").removePrefix("I")
            } else if (clean.startsWith("1 NAME")) {
                activeName = clean.removePrefix("1 NAME").trim().replace("/", "")
            } else if (clean.startsWith("1 SEX")) {
                activeGender = when (clean.removePrefix("1 SEX").trim().uppercase()) {
                    "M" -> Gender.MALE
                    "F" -> Gender.FEMALE
                    else -> Gender.UNKNOWN
                }
            } else if (clean == "1 BIRT") {
                lastEvent = "BIRT"
            } else if (clean == "1 DEAT") {
                lastEvent = "DEAT"
            } else if (clean.startsWith("2 DATE")) {
                if (lastEvent == "BIRT") birthDate = clean.removePrefix("2 DATE").trim()
                if (lastEvent == "DEAT") deathDate = clean.removePrefix("2 DATE").trim()
            } else if (clean.startsWith("2 PLAC")) {
                if (lastEvent == "BIRT") birthPlace = clean.removePrefix("2 PLAC").trim()
                if (lastEvent == "DEAT") deathPlace = clean.removePrefix("2 PLAC").trim()
            } else if (clean.startsWith("1 NOTE")) {
                note = clean.removePrefix("1 NOTE").trim()
            }
        }
        flush()
        return current.copy(members = (current.members + members).distinctBy { it.id })
    }
}

private fun Gender.toGedcomSex(): String = when (this) {
    Gender.MALE -> "M"
    Gender.FEMALE -> "F"
    else -> "U"
}

private fun String.safeGedcomId(): String = filter { it.isLetterOrDigit() }.ifBlank { hashCode().toString().replace("-", "") }
private fun String.escapeGedcom(): String = replace("\n", " ").replace("\r", " ").trim()
