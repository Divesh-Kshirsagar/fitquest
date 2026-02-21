package com.sdevprem.runtrack.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sdevprem.runtrack.ui.common.compose.component.RunTrackTopBar

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val saveStatus by viewModel.saveStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(saveStatus) {
        when (val status = saveStatus) {
            is EditProfileViewModel.SaveStatus.Success -> {
                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            is EditProfileViewModel.SaveStatus.Error -> {
                Toast.makeText(context, status.msg, Toast.LENGTH_SHORT).show()
                viewModel.resetSaveStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            RunTrackTopBar(
                title = "Personal Parameters",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            OutlinedTextField(
                value = user.name,
                onValueChange = viewModel::updateName,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            Spacer(modifier = Modifier.size(16.dp))

            // Weight Input
            var weightInput by remember(user.weightInKg) { mutableStateOf(if (user.weightInKg > 0) user.weightInKg.toString() else "") }
            OutlinedTextField(
                value = weightInput,
                onValueChange = {
                    weightInput = it
                    if (it.isBlank()) viewModel.updateWeight(0f)
                    else it.toFloatOrNull()?.let { w -> viewModel.updateWeight(w) }
                },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )

            Spacer(modifier = Modifier.size(16.dp))

            // Weekly Goal Input
            var goalInput by remember(user.weeklyGoalInKM) { mutableStateOf(if (user.weeklyGoalInKM > 0) user.weeklyGoalInKM.toString() else "") }
            OutlinedTextField(
                value = goalInput,
                onValueChange = {
                    goalInput = it
                    if (it.isBlank()) viewModel.updateWeeklyGoal(0f)
                    else it.toFloatOrNull()?.let { g -> viewModel.updateWeeklyGoal(g) }
                },
                label = { Text("Weekly Goal (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                singleLine = true
            )

            Spacer(modifier = Modifier.size(24.dp))

            Button(
                onClick = viewModel::saveUser,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Changes")
            }
        }
    }
}
