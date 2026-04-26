package com.famy.tree.data

import com.famy.tree.data.model.EventType
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.Gender
import com.famy.tree.data.model.LifeEvent
import com.famy.tree.data.model.Relationship
import com.famy.tree.data.model.RelationshipType

object SampleData {
    fun demo(): FamilyState {
        val arun = FamilyMember(
            id = "arun",
            displayName = "Arun Patel",
            gender = Gender.MALE,
            birthDate = "1938",
            birthPlace = "Ahmedabad",
            deathDate = "2012",
            notes = "Loved gardening, railway maps, and telling stories around dinner.",
            events = listOf(LifeEvent(type = EventType.BIRTH, title = "Birth", date = "1938", place = "Ahmedabad"))
        )
        val meera = FamilyMember(id = "meera", displayName = "Meera Patel", gender = Gender.FEMALE, birthDate = "1942", birthPlace = "Surat")
        val dev = FamilyMember(id = "dev", displayName = "Dev Patel", gender = Gender.MALE, birthDate = "1968", birthPlace = "Mumbai")
        val nisha = FamilyMember(id = "nisha", displayName = "Nisha Rao", gender = Gender.FEMALE, birthDate = "1971", birthPlace = "Pune")
        val leena = FamilyMember(id = "leena", displayName = "Leena Patel", gender = Gender.FEMALE, birthDate = "1996", birthPlace = "Bengaluru")
        val kai = FamilyMember(id = "kai", displayName = "Kai Patel", gender = Gender.NON_BINARY, birthDate = "2002", birthPlace = "Bengaluru")
        val members = listOf(arun, meera, dev, nisha, leena, kai)
        return FamilyState(
            tree = FamilyState().tree.copy(name = "Patel Family", rootMemberId = arun.id),
            members = members,
            relationships = listOf(
                Relationship(sourceMemberId = arun.id, targetMemberId = meera.id, type = RelationshipType.SPOUSE, startDate = "1964"),
                Relationship(sourceMemberId = arun.id, targetMemberId = dev.id, type = RelationshipType.PARENT_CHILD),
                Relationship(sourceMemberId = meera.id, targetMemberId = dev.id, type = RelationshipType.PARENT_CHILD),
                Relationship(sourceMemberId = dev.id, targetMemberId = nisha.id, type = RelationshipType.SPOUSE, startDate = "1994"),
                Relationship(sourceMemberId = dev.id, targetMemberId = leena.id, type = RelationshipType.PARENT_CHILD),
                Relationship(sourceMemberId = nisha.id, targetMemberId = leena.id, type = RelationshipType.PARENT_CHILD),
                Relationship(sourceMemberId = dev.id, targetMemberId = kai.id, type = RelationshipType.PARENT_CHILD),
                Relationship(sourceMemberId = nisha.id, targetMemberId = kai.id, type = RelationshipType.PARENT_CHILD),
                Relationship(sourceMemberId = leena.id, targetMemberId = kai.id, type = RelationshipType.SIBLING)
            )
        )
    }
}
