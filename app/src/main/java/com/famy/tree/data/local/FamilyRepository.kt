package com.famy.tree.data.local

import com.famy.tree.data.model.AppSettings
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.LifeEvent
import com.famy.tree.data.model.MediaItem
import com.famy.tree.data.model.Relationship

interface FamilyRepository {
    suspend fun load(): FamilyState
    suspend fun save(state: FamilyState)
    suspend fun exportJson(): String
    suspend fun importJson(json: String): FamilyState
    suspend fun replace(state: FamilyState)
    suspend fun clear(): FamilyState
}
