package com.example.taskreminderapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskreminderapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao //data access object
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Query("SELECT * FROM Reminder ORDER BY timeInMillis DESC")
    fun getAllReminder(): Flow<List<Reminder>>
}