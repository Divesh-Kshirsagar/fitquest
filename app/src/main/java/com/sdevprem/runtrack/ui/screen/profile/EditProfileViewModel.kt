package com.sdevprem.runtrack.ui.screen.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.data.model.Gender
import com.sdevprem.runtrack.data.model.User
import com.sdevprem.runtrack.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus = _saveStatus.asStateFlow()

    init {
        userRepository.user
            .onEach { dbUser -> _user.update { dbUser } }
            .launchIn(viewModelScope)
    }

    fun updateName(name: String) = _user.update { it.copy(name = name) }
    fun updateGender(gender: Gender) = _user.update { it.copy(gender = gender) }
    fun updateWeight(weightInKg: Float) = _user.update { it.copy(weightInKg = weightInKg) }
    fun updateWeeklyGoal(weeklyGoalInKm: Float) =
        _user.update { it.copy(weeklyGoalInKM = weeklyGoalInKm) }
    fun updateImgUri(uri: Uri?) = _user.update { it.copy(imgUri = uri) }

    fun saveUser() {
        if (_user.value.name.isBlank()) {
            _saveStatus.value = SaveStatus.Error("Name cannot be empty")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.updateUser(_user.value)
                _saveStatus.value = SaveStatus.Success
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }

    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Success : SaveStatus()
        data class Error(val msg: String) : SaveStatus()
    }
}
