package com.example.taskreminderapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import com.example.taskreminderapp.domain.model.Reminder

@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDatabase: RoomDatabase()  {
    //making database and creating instances..
    companion object{
        fun getInstance(context: Context) =
            Room.databaseBuilder(context, ReminderDatabase::class.java, "reminder")
                .build()
    }
    abstract fun getReminderDao(): ReminderDao
}