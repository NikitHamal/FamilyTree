package com.famy.tree.data

import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.RelationshipType

object FamilyStats {
    fun totalMembers(state: FamilyState): Int = state.members.size

    fun generationCount(state: FamilyState): Int {
        val depths = generationMap(state)
        return depths.values.maxOrNull()?.plus(1) ?: 0
    }

    fun livingCount(state: FamilyState): Int = state.members.count { it.isLiving }

    fun recentMembers(state: FamilyState, limit: Int = 5): List<FamilyMember> {
        return state.members.sortedByDescending { it.createdAt }.take(limit)
    }

    fun commonNames(state: FamilyState): List<Pair<String, Int>> {
        return state.members
            .flatMap { it.displayName.split(" ") }
            .map { it.trim() }
            .filter { it.length > 1 }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(8)
            .map { it.key to it.value }
    }

    fun averageLifespan(state: FamilyState): Int? {
        val ages = state.members.mapNotNull { member ->
            val birth = member.birthDate.extractYear()
            val death = member.deathDate.extractYear()
            if (birth != null && death != null && death >= birth) death - birth else null
        }
        return ages.takeIf { it.isNotEmpty() }?.average()?.toInt()
    }

    fun generationMap(state: FamilyState): Map<String, Int> {
        val members = state.members.associateBy { it.id }
        if (members.isEmpty()) return emptyMap()
        val childIds = state.relationships
            .filter { it.type == RelationshipType.PARENT_CHILD }
            .map { it.targetMemberId }
            .toSet()
        val roots = members.keys.filterNot { it in childIds }.ifEmpty { members.keys.take(1) }
        val graph = state.relationships
            .filter { it.type == RelationshipType.PARENT_CHILD }
            .groupBy { it.sourceMemberId }
            .mapValues { entry -> entry.value.map { it.targetMemberId } }
        val depths = mutableMapOf<String, Int>()
        val queue = ArrayDeque<Pair<String, Int>>()
        roots.forEach { queue.add(it to 0) }
        while (queue.isNotEmpty()) {
            val (id, depth) = queue.removeFirst()
            val current = depths[id]
            if (current != null && current <= depth) continue
            depths[id] = depth
            graph[id].orEmpty().forEach { queue.add(it to depth + 1) }
        }
        members.keys.filterNot { it in depths }.forEach { depths[it] = 0 }
        return depths
    }
}

fun String.extractYear(): Int? {
    val match = Regex("(\\d{4})").find(this)
    return match?.value?.toIntOrNull()
}
