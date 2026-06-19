package com.mantapp.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mantapp.app.ui.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val dashboardState = DashboardUiState()
}
