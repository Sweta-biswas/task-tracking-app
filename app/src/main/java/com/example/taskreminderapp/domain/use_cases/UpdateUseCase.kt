package com.example.taskreminderapp.domain.use_cases

import com.example.taskreminderapp.domain.model.Reminder
import com.example.taskreminderapp.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {

    suspend operator fun invoke(reminder: Reminder) = reminderRepository.update(reminder)
}