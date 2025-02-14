package com.example.taskreminderapp.presentation.ui

import androidx.lifecycle.ViewModel //ui data manage with the help of lifecycle events
import com.example.taskreminderapp.domain.use_cases.DeleteUseCase
import com.example.taskreminderapp.domain.use_cases.GetAllReminderUseCase
import com.example.taskreminderapp.domain.use_cases.InsertUseCase
import com.example.taskreminderapp.domain.use_cases.UpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//presentation layer - domain layer
@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertUseCase: InsertUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val updateUseCase: UpdateUseCase,
    private val getAllReminderUseCase: GetAllReminderUseCase

) : ViewModel(){
}