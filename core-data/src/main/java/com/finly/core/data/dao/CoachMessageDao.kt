package com.finly.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finly.core.data.entity.CoachMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachMessageDao {
    @Query("SELECT * FROM coach_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<CoachMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessage(message: CoachMessageEntity)

    @Query("DELETE FROM coach_messages WHERE sessionId = :sessionId")
    suspend fun clearSession(sessionId: String)

    @Query("DELETE FROM coach_messages")
    suspend fun deleteAllMessages()
}
