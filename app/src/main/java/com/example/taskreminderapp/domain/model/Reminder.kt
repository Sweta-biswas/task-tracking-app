package com.example.taskreminderapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    val title: String,
    val description: String,
    @PrimaryKey(autoGenerate = false)
    val timeInMillis: Long,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
)
