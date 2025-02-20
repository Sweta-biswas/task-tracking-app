package com.example.taskreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.layout.Arrangement
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskreminderapp.domain.model.Reminder
import com.example.taskreminderapp.presentation.ui.setUpAlarm
import com.example.taskreminderapp.presentation.ui.setUpPeriodicAlarm
import com.example.taskreminderapp.presentation.ui.cancelAlarm
import com.example.taskreminderapp.presentation.MainViewModel
import com.example.taskreminderapp.ui.theme.TaskReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskReminderAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = hiltViewModel<MainViewModel>()
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isTimePickerVisible = remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val format = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeInMillis = remember { mutableStateOf(0L) }
    val isRecurring = remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Form(
                time = format.format(timeInMillis.value),
                onTimeClick = {
                    isTimePickerVisible.value = true
                }
            ) { title, description, isRecurringTask ->
                scope.launch(Dispatchers.IO) {
                    val reminder = Reminder(
                        title = title,
                        description = description,
                        timeInMillis = timeInMillis.value,
                        isCompleted = false,
                        isRecurring = isRecurringTask
                    )
                    viewModel.insert(reminder)

                    withContext(Dispatchers.Main) {
                        if (isRecurringTask) {
                            setUpPeriodicAlarm(context, reminder)
                        } else {
                            setUpAlarm(context, reminder)
                        }
                    }
                }
                scope.launch { sheetState.hide() }
            }
        }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Task Reminder") },
                    actions = {
                        IconButton(onClick = {
                            scope.launch { sheetState.show() }
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                        }
                    })
            }) { paddingValues ->
            if (isTimePickerVisible.value) {
                Dialog(onDismissRequest = { isTimePickerVisible.value = false }) {
                    Surface(
                        modifier = Modifier.padding(16.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            TimePicker(
                                state = timePickerState
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    isTimePickerVisible.value = false
                                }) {
                                    Text("Cancel")
                                }
                                TextButton(onClick = {
                                    val calendar = Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                        set(Calendar.MINUTE, timePickerState.minute)
                                    }
                                    timeInMillis.value = calendar.timeInMillis
                                    isTimePickerVisible.value = false
                                }) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Task")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    items(uiState.data) { reminder ->
                        Card(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = reminder.title)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = reminder.description)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = format.format(reminder.timeInMillis))
                                }
                                if (reminder.isRecurring) {
                                    IconButton(onClick = {
                                        cancelAlarm(context, reminder)
                                        viewModel.update(reminder.copy(isCompleted = true, isRecurring = false))
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_schedule),
                                            contentDescription = "Cancel Recurring"
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    cancelAlarm(context, reminder)
                                    viewModel.delete(reminder)
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delete),
                                        contentDescription = "Delete Task"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Form(
    time: String,
    onTimeClick: () -> Unit,
    onClick: (String, String, Boolean) -> Unit
) {
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val isRecurring = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = 24.dp, start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = title.value,
            onValueChange = {
                title.value = it
                isError.value = false
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Title") },
            isError = isError.value && title.value.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description.value,
            onValueChange = {
                description.value = it
                isError.value = false
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Description") },
            isError = isError.value && description.value.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = time,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTimeClick() },
            label = { Text("Time") },
            enabled = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Recurring Schedule")
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isRecurring.value,
                onCheckedChange = { isRecurring.value = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            isError.value = title.value.isBlank() || description.value.isBlank()
            if (!isError.value) {
                onClick(title.value, description.value, isRecurring.value)
            }
        }) {
            Text(text = "Save")
        }

        if (isError.value) {
            Text(
                text = "Please fill in all required fields",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}