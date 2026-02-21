package com.sdevprem.runtrack.domain.demo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoModeManager @Inject constructor() {
    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode = _isDemoMode.asStateFlow()

    fun setDemoMode(isEnabled: Boolean) {
        _isDemoMode.update { isEnabled }
    }

    fun toggle() {
        _isDemoMode.update { !it }
    }
}
