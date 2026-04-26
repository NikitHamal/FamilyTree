package com.famy.tree.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.famy.tree.data.GedcomCodec
import com.famy.tree.data.SampleData
import com.famy.tree.data.extractYear
import com.famy.tree.data.local.LocalFamilyRepository
import com.famy.tree.data.model.AppSettings
import com.famy.tree.data.model.EventType
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.LifeEvent
import com.famy.tree.data.model.MediaItem
import com.famy.tree.data.model.Relationship
import com.famy.tree.data.model.RelationshipType
import com.famy.tree.data.model.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FamyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LocalFamilyRepository(application)
    private val _state = MutableStateFlow(FamyUiState(isLoading = true))
    val state: StateFlow<FamyUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val loaded = repository.load()
            _state.value = FamyUiState(familyState = loaded, isLoading = false)
        }
    }

    fun completeOnboarding() = mutate { it.copy(settings = it.settings.copy(onboardingComplete = true)) }
    fun setTheme(preference: ThemePreference) = mutate { it.copy(settings = it.settings.copy(themePreference = preference)) }
    fun setCompactCards(enabled: Boolean) = mutate { it.copy(settings = it.settings.copy(compactTreeCards = enabled)) }
    fun setDateFormat(format: String) = mutate { it.copy(settings = it.settings.copy(dateFormat = format)) }

    fun addDemoTree() = replace(SampleData.demo().copy(settings = _state.value.familyState.settings.copy(onboardingComplete = true)))
    fun clearAll() = viewModelScope.launch { _state.value = FamyUiState(familyState = repository.clear()) }

    fun saveMember(member: FamilyMember) = mutate { state ->
        val existing = state.members.any { it.id == member.id }
        val stamped = member.copy(updatedAt = System.currentTimeMillis())
        if (existing) state.copy(members = state.members.map { if (it.id == member.id) stamped else it })
        else state.copy(members = state.members + stamped.copy(createdAt = System.currentTimeMillis()))
    }

    fun removeMember(memberId: String) = mutate { state ->
        state.copy(
            members = state.members.filterNot { it.id == memberId },
            relationships = state.relationships.filterNot { it.sourceMemberId == memberId || it.targetMemberId == memberId },
            media = state.media.filterNot { it.memberId == memberId }
        )
    }

    fun addRelationship(relationship: Relationship) {
        val validation = validateRelationship(relationship)
        if (validation != null) {
            _state.update { it.copy(message = validation) }
            return
        }
        mutate { state -> state.copy(relationships = state.relationships + relationship) }
    }

    fun removeRelationship(id: String) = mutate { state -> state.copy(relationships = state.relationships.filterNot { it.id == id }) }

    fun addMedia(media: MediaItem) = mutate { state -> state.copy(media = state.media + media) }

    fun addEvent(memberId: String, event: LifeEvent) = mutate { state ->
        state.copy(members = state.members.map { member ->
            if (member.id == memberId) member.copy(events = member.events + event, updatedAt = System.currentTimeMillis()) else member
        })
    }

    fun exportJson(onReady: (String) -> Unit) {
        viewModelScope.launch { onReady(repository.exportJson()) }
    }

    fun importJson(raw: String) {
        viewModelScope.launch {
            runCatching { repository.importJson(raw) }
                .onSuccess { _state.value = FamyUiState(familyState = it, message = "Backup restored") }
                .onFailure { _state.update { current -> current.copy(message = it.message ?: "Invalid JSON backup") } }
        }
    }

    fun exportGedcom(onReady: (String) -> Unit) {
        onReady(GedcomCodec.export(_state.value.familyState))
    }

    fun importGedcom(raw: String) {
        val merged = GedcomCodec.import(raw, _state.value.familyState)
        replace(merged)
    }

    fun clearMessage() = _state.update { it.copy(message = null) }

    private fun replace(state: FamilyState) {
        viewModelScope.launch {
            repository.replace(state)
            _state.value = FamyUiState(familyState = state)
        }
    }

    private fun mutate(block: (FamilyState) -> FamilyState) {
        viewModelScope.launch {
            val next = block(_state.value.familyState)
            repository.save(next)
            _state.update { it.copy(familyState = next, isLoading = false) }
        }
    }

    private fun validateRelationship(relationship: Relationship): String? {
        val state = _state.value.familyState
        if (relationship.sourceMemberId == relationship.targetMemberId) return "A person cannot be related to themselves."
        val members = state.members.associateBy { it.id }
        val source = members[relationship.sourceMemberId] ?: return "Choose the first member."
        val target = members[relationship.targetMemberId] ?: return "Choose the second member."
        val duplicate = state.relationships.any {
            it.type == relationship.type &&
                ((it.sourceMemberId == relationship.sourceMemberId && it.targetMemberId == relationship.targetMemberId) ||
                    (relationship.type != RelationshipType.PARENT_CHILD && it.sourceMemberId == relationship.targetMemberId && it.targetMemberId == relationship.sourceMemberId))
        }
        if (duplicate) return "That relationship already exists."
        if (relationship.type == RelationshipType.PARENT_CHILD) {
            val parentYear = source.birthDate.extractYear()
            val childYear = target.birthDate.extractYear()
            if (parentYear != null && childYear != null && parentYear + 12 > childYear) {
                return "Parent-child dates look impossible. Check the birth years."
            }
            val wouldCycle = wouldCreateAncestorCycle(relationship.sourceMemberId, relationship.targetMemberId)
            if (wouldCycle) return "This parent-child link would create a cycle."
        }
        return null
    }

    private fun wouldCreateAncestorCycle(parentId: String, childId: String): Boolean {
        val childrenByParent = _state.value.familyState.relationships
            .filter { it.type == RelationshipType.PARENT_CHILD }
            .groupBy { it.sourceMemberId }
            .mapValues { entry -> entry.value.map { it.targetMemberId } }
        val queue = ArrayDeque<String>()
        queue.add(childId)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current == parentId) return true
            childrenByParent[current].orEmpty().forEach(queue::add)
        }
        return false
    }
}

data class FamyUiState(
    val familyState: FamilyState = FamilyState(),
    val isLoading: Boolean = false,
    val message: String? = null
)
