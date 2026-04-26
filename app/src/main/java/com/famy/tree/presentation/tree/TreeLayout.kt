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
    val width: Float = 176f,
    val height: Float = 84f
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
        val generationMap = FamilyStats.generationMap(state)
        val groups = state.members.groupBy { generationMap[it.id] ?: 0 }.toSortedMap()
        val nodes = when (mode) {
            TreeLayoutMode.VERTICAL -> vertical(groups)
            TreeLayoutMode.HORIZONTAL -> horizontal(groups)
            TreeLayoutMode.FAN -> fan(groups)
            TreeLayoutMode.PEDIGREE -> pedigree(groups, state.tree.rootMemberId)
        }
        val byId = nodes.associateBy { it.member.id }
        val edges = state.relationships.mapNotNull { relationship ->
            val start = byId[relationship.sourceMemberId]?.center
            val end = byId[relationship.targetMemberId]?.center
            if (start != null && end != null) TreeEdgeLayout(start, end, relationship.type) else null
        }
        return TreeLayoutResult(nodes, edges)
    }

    private fun vertical(groups: Map<Int, List<FamilyMember>>): List<TreeNodeLayout> {
        val ySpacing = 170f
        val xSpacing = 230f
        return groups.flatMap { (generation, members) ->
            members.sortedBy { it.displayName }.mapIndexed { index, member ->
                val offset = index - (members.size - 1) / 2f
                TreeNodeLayout(member, Offset(offset * xSpacing, generation * ySpacing), generation)
            }
        }
    }

    private fun horizontal(groups: Map<Int, List<FamilyMember>>): List<TreeNodeLayout> {
        val ySpacing = 140f
        val xSpacing = 250f
        return groups.flatMap { (generation, members) ->
            members.sortedBy { it.displayName }.mapIndexed { index, member ->
                val offset = index - (members.size - 1) / 2f
                TreeNodeLayout(member, Offset(generation * xSpacing, offset * ySpacing), generation)
            }
        }
    }

    private fun fan(groups: Map<Int, List<FamilyMember>>): List<TreeNodeLayout> {
        return groups.flatMap { (generation, members) ->
            val radius = 20f + generation * 145f
            val sorted = members.sortedBy { it.displayName }
            sorted.mapIndexed { index, member ->
                if (generation == 0 && sorted.size == 1) {
                    TreeNodeLayout(member, Offset.Zero, generation)
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
        val base = vertical(groups)
        if (rootId == null) return base
        return base.sortedByDescending { if (it.member.id == rootId) 1 else 0 }
    }
}
