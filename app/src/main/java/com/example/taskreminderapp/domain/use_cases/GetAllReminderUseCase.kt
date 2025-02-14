package com.example.taskreminderapp.domain.use_cases

import com.example.taskreminderapp.domain.repository.ReminderRepository
import javax.inject.Inject
//dependency injection dagger hilt
class GetAllReminderUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {

    operator fun invoke() = reminderRepository.getAllReminders()
}