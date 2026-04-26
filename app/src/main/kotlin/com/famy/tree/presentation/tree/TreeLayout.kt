package com.famy.tree.presentation.tree

import androidx.compose.ui.geometry.Offset
import com.famy.tree.data.FamilyStats
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.RelationshipType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class TreeLayoutMode { VERTICAL, HORIZONTAL, FAN, PEDIGREE }

data class TreeNodeLayout(
    val member: FamilyMember,
    val center: Offset,
    val generation: Int,
    val width: Float = 204f,
    val height: Float = 88f
) {
    fun contains(point: Offset): Boolean {
        return point.x >= center.x - width / 2f && point.x <= center.x + width / 2f &&
            point.y >= center.y - height / 2f && point.y <= center.y + height / 2f
    }
}

data class TreeEdgeLayout(val start: Offset, val end: Offset, val type: RelationshipType)

data class TreeLayoutResult(val nodes: List<TreeNodeLayout>, val edges: List<TreeEdgeLayout>)

object FamilyTreeLayoutEngine {
    fun compute(state: FamilyState, mode: TreeLayoutMode): TreeLayoutResult {
        if (state.members.isEmpty()) return TreeLayoutResult(emptyList(), emptyList())
        val rootId = state.tree.rootMemberId ?: chooseDefaultRoot(state)
        val generationMap = when (mode) {
            TreeLayoutMode.PEDIGREE -> focusedGenerationMap(state, rootId)
            else -> FamilyStats.generationMap(state)
        }
        val groups = state.members.groupBy { generationMap[it.id] ?: 0 }.toSortedMap()
        val nodes = when (mode) {
            TreeLayoutMode.VERTICAL -> vertical(groups, rootId)
            TreeLayoutMode.HORIZONTAL -> horizontal(groups, rootId)
            TreeLayoutMode.FAN -> fan(groups, rootId)
            TreeLayoutMode.PEDIGREE -> pedigree(groups, rootId)
        }
        val byId = nodes.associateBy { it.member.id }
        val edges = state.relationships.mapNotNull { relationship ->
            val start = byId[relationship.sourceMemberId]?.center
            val end = byId[relationship.targetMemberId]?.center
            if (start != null && end != null) TreeEdgeLayout(start, end, relationship.type) else null
        }
        return TreeLayoutResult(nodes, edges)
    }

    private fun chooseDefaultRoot(state: FamilyState): String? {
        if (state.members.isEmpty()) return null
        val score = state.relationships.flatMap { listOf(it.sourceMemberId, it.targetMemberId) }
            .groupingBy { it }
            .eachCount()
        return state.members.maxByOrNull { score[it.id] ?: 0 }?.id ?: state.members.first().id
    }

    private fun focusedGenerationMap(state: FamilyState, rootId: String?): Map<String, Int> {
        if (rootId == null) return FamilyStats.generationMap(state)
        val parentsByChild = state.relationships
            .filter { it.type == RelationshipType.PARENT_CHILD }
            .groupBy { it.targetMemberId }
            .mapValues { entry -> entry.value.map { it.sourceMemberId } }
        val childrenByParent = state.relationships
            .filter { it.type == RelationshipType.PARENT_CHILD }
            .groupBy { it.sourceMemberId }
            .mapValues { entry -> entry.value.map { it.targetMemberId } }
        val spouses = buildMap<String, MutableList<String>> {
            state.relationships.filter { it.type == RelationshipType.SPOUSE }.forEach { relation ->
                getOrPut(relation.sourceMemberId) { mutableListOf() }.add(relation.targetMemberId)
                getOrPut(relation.targetMemberId) { mutableListOf() }.add(relation.sourceMemberId)
            }
        }
        val siblings = buildMap<String, MutableList<String>> {
            state.relationships.filter { it.type == RelationshipType.SIBLING }.forEach { relation ->
                getOrPut(relation.sourceMemberId) { mutableListOf() }.add(relation.targetMemberId)
                getOrPut(relation.targetMemberId) { mutableListOf() }.add(relation.sourceMemberId)
            }
        }

        val result = mutableMapOf(rootId to 0)
        val queue = ArrayDeque<String>()
        queue.add(rootId)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val currentGeneration = result[current] ?: 0
            parentsByChild[current].orEmpty().forEach { parent ->
                if (result.putIfAbsent(parent, currentGeneration - 1) == null) queue.add(parent)
            }
            childrenByParent[current].orEmpty().forEach { child ->
                if (result.putIfAbsent(child, currentGeneration + 1) == null) queue.add(child)
            }
            spouses[current].orEmpty().forEach { spouse ->
                if (result.putIfAbsent(spouse, currentGeneration) == null) queue.add(spouse)
            }
            siblings[current].orEmpty().forEach { sibling ->
                if (result.putIfAbsent(sibling, currentGeneration) == null) queue.add(sibling)
            }
        }

        val fallback = FamilyStats.generationMap(state)
        state.members.forEach { member -> result.putIfAbsent(member.id, fallback[member.id] ?: 0) }
        return result
    }

    private fun orderedMembers(members: List<FamilyMember>, rootId: String?): List<FamilyMember> {
        return members.sortedWith(
            compareBy<FamilyMember>(
                { if (it.id == rootId) 0 else 1 },
                { it.familyName.ifBlank { it.displayName } },
                { it.displayName }
            )
        )
    }

    private fun vertical(groups: Map<Int, List<FamilyMember>>, rootId: String?): List<TreeNodeLayout> {
        val ySpacing = 176f
        val xSpacing = 244f
        return groups.flatMap { (generation, members) ->
            val sorted = orderedMembers(members, rootId)
            sorted.mapIndexed { index, member ->
                val offset = index - (sorted.size - 1) / 2f
                TreeNodeLayout(member, Offset(offset * xSpacing, generation * ySpacing), generation)
            }
        }
    }

    private fun horizontal(groups: Map<Int, List<FamilyMember>>, rootId: String?): List<TreeNodeLayout> {
        val ySpacing = 152f
        val xSpacing = 254f
        return groups.flatMap { (generation, members) ->
            val sorted = orderedMembers(members, rootId)
            sorted.mapIndexed { index, member ->
                val offset = index - (sorted.size - 1) / 2f
                TreeNodeLayout(member, Offset(generation * xSpacing, offset * ySpacing), generation)
            }
        }
    }

    private fun fan(groups: Map<Int, List<FamilyMember>>, rootId: String?): List<TreeNodeLayout> {
        return groups.flatMap { (generation, members) ->
            val radius = 35f + generation * 156f
            val sorted = orderedMembers(members, rootId)
            sorted.mapIndexed { index, member ->
                if (generation == 0 && sorted.any { it.id == rootId }) {
                    val rootIndex = sorted.indexOfFirst { it.id == rootId }
                    if (index == rootIndex) {
                        TreeNodeLayout(member, Offset.Zero, generation)
                    } else {
                        val spread = PI * 1.35
                        val start = -PI * 0.675
                        val angle = start + spread * ((index + 1).toDouble() / (sorted.size + 1).toDouble())
                        TreeNodeLayout(member, Offset((cos(angle) * 120f).toFloat(), (sin(angle) * 120f).toFloat()), generation)
                    }
                } else {
                    val spread = PI * 1.55
                    val start = -PI * 0.775
                    val angle = start + spread * ((index + 1).toDouble() / (sorted.size + 1).toDouble())
                    TreeNodeLayout(member, Offset((cos(angle) * radius).toFloat(), (sin(angle) * radius).toFloat()), generation)
                }
            }
        }
    }

    private fun pedigree(groups: Map<Int, List<FamilyMember>>, rootId: String?): List<TreeNodeLayout> {
        val ySpacing = 184f
        val xSpacing = 224f
        return groups.flatMap { (generation, members) ->
            val sorted = orderedMembers(members, rootId)
            sorted.mapIndexed { index, member ->
                val offset = index - (sorted.size - 1) / 2f
                TreeNodeLayout(member, Offset(offset * xSpacing, generation * ySpacing), generation)
            }
        }
    }
}
