package com.example.taskreminderapp.data.di

import android.content.Context
import com.example.taskreminderapp.data.local.ReminderDao
import com.example.taskreminderapp.data.local.ReminderDatabase
import com.example.taskreminderapp.data.repository.ReminderRepoImpl
import com.example.taskreminderapp.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context):ReminderDatabase{
        return ReminderDatabase.getInstance(context)
    }
    @Provides
    fun provideDao(reminderDatabase: ReminderDatabase): ReminderDao {
        return reminderDatabase.getReminderDao()
    }
    @Provides
    fun provideReminderRepo(reminderDao: ReminderDao) : ReminderRepository {
        return ReminderRepoImpl(reminderDao)
    }
}