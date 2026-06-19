package com.mantapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantapp.app.ui.event.AuthEvent
import com.mantapp.app.ui.state.AuthDestination
import com.mantapp.app.ui.state.AuthUiState
import com.mantapp.app.ui.state.ScreenStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registrationState = MutableStateFlow(AuthUiState())
    val registrationState: StateFlow<AuthUiState> = _registrationState.asStateFlow()

    fun onLoginEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> updateLogin { copy(email = event.value, errorMessage = null) }
            is AuthEvent.PasswordChanged -> updateLogin { copy(password = event.value, errorMessage = null) }
            is AuthEvent.ReturningOnboardedUserChanged -> {
                updateLogin { copy(returningOnboardedUser = event.value) }
            }
            AuthEvent.SubmitLogin -> submitLogin()
            AuthEvent.NavigationHandled -> updateLogin { copy(destination = null, status = ScreenStatus.Idle) }
            else -> Unit
        }
    }

    fun onRegistrationEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.DisplayNameChanged -> updateRegistration {
                copy(displayName = event.value, errorMessage = null)
            }
            is AuthEvent.EmailChanged -> updateRegistration { copy(email = event.value, errorMessage = null) }
            is AuthEvent.PasswordChanged -> updateRegistration {
                copy(password = event.value, errorMessage = null)
            }
            is AuthEvent.ConfirmPasswordChanged -> updateRegistration {
                copy(confirmPassword = event.value, errorMessage = null)
            }
            AuthEvent.SubmitRegistration -> submitRegistration()
            AuthEvent.NavigationHandled -> updateRegistration {
                copy(destination = null, status = ScreenStatus.Idle)
            }
            else -> Unit
        }
    }

    private fun submitLogin() {
        val state = _loginState.value
        val validationError = validateCredentials(state.email, state.password)
        if (validationError != null) {
            updateLogin {
                copy(status = ScreenStatus.Error, errorMessage = validationError, successMessage = null)
            }
            return
        }

        updateLogin { copy(status = ScreenStatus.Loading, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            updateLogin {
                copy(
                    status = ScreenStatus.Success,
                    errorMessage = null,
                    successMessage = "Signed in locally.",
                    destination = if (returningOnboardedUser) {
                        AuthDestination.Dashboard
                    } else {
                        AuthDestination.Onboarding
                    },
                )
            }
        }
    }

    private fun submitRegistration() {
        val state = _registrationState.value
        val validationError = validateRegistration(state)
        if (validationError != null) {
            updateRegistration {
                copy(status = ScreenStatus.Error, errorMessage = validationError, successMessage = null)
            }
            return
        }

        updateRegistration { copy(status = ScreenStatus.Loading, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            updateRegistration {
                copy(
                    status = ScreenStatus.Success,
                    errorMessage = null,
                    successMessage = "Profile created locally.",
                    destination = AuthDestination.Onboarding,
                )
            }
        }
    }

    private fun validateRegistration(state: AuthUiState): String? {
        if (state.displayName.isBlank()) return "Enter your name."
        val credentialError = validateCredentials(state.email, state.password)
        if (credentialError != null) return credentialError
        if (state.password != state.confirmPassword) return "Passwords do not match."
        return null
    }

    private fun validateCredentials(email: String, password: String): String? {
        if (email.isBlank()) return "Enter your email."
        if (!email.contains("@")) return "Enter a valid email address."
        if (password.isBlank()) return "Enter your password."
        if (password.length < MIN_PASSWORD_LENGTH) {
            return "Use at least $MIN_PASSWORD_LENGTH characters for the password."
        }
        return null
    }

    private fun updateLogin(reducer: AuthUiState.() -> AuthUiState) {
        _loginState.update { current -> current.reducer() }
    }

    private fun updateRegistration(reducer: AuthUiState.() -> AuthUiState) {
        _registrationState.update { current -> current.reducer() }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}
