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
            is AuthEvent.EmailChanged -> updateLogin {
                copy(email = event.value, emailError = null, errorMessage = null)
            }
            is AuthEvent.PasswordChanged -> updateLogin {
                copy(password = event.value, passwordError = null, errorMessage = null)
            }
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
                copy(displayName = event.value, displayNameError = null, errorMessage = null)
            }
            is AuthEvent.EmailChanged -> updateRegistration {
                copy(email = event.value, emailError = null, errorMessage = null)
            }
            is AuthEvent.PasswordChanged -> updateRegistration {
                copy(password = event.value, passwordError = null, errorMessage = null)
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
        val validation = validateCredentials(state.email, state.password)
        if (!validation.isValid) {
            updateLogin {
                copy(
                    status = ScreenStatus.Error,
                    emailError = validation.emailError,
                    passwordError = validation.passwordError,
                    errorMessage = "Check the highlighted details and try again.",
                    successMessage = null,
                )
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
        val validation = validateRegistration(state)
        if (!validation.isValid) {
            updateRegistration {
                copy(
                    status = ScreenStatus.Error,
                    displayNameError = validation.displayNameError,
                    emailError = validation.emailError,
                    passwordError = validation.passwordError,
                    errorMessage = "Check the highlighted details and try again.",
                    successMessage = null,
                )
            }
            return
        }

        updateRegistration { copy(status = ScreenStatus.Loading, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            updateLogin {
                copy(
                    displayName = state.displayName.trim(),
                    email = state.email.trim(),
                    status = ScreenStatus.Success,
                    errorMessage = null,
                    successMessage = "Signed in locally.",
                    destination = null,
                )
            }
            updateRegistration {
                copy(
                    status = ScreenStatus.Success,
                    errorMessage = null,
                    successMessage = "Account created. Taking you to setup.",
                    destination = AuthDestination.Onboarding,
                )
            }
        }
    }

    private fun validateRegistration(state: AuthUiState): AuthValidationResult {
        val credentialResult = validateCredentials(state.email, state.password)
        return credentialResult.copy(
            displayNameError = if (state.displayName.isBlank()) {
                "Enter your full name or username."
            } else {
                null
            },
        )
    }

    private fun validateCredentials(email: String, password: String): AuthValidationResult {
        val emailError = when {
            email.isBlank() -> "Enter your email address."
            !email.contains("@") -> "Use a valid email address."
            else -> null
        }
        val passwordError = when {
            password.isBlank() -> "Enter your password."
            password.length < MIN_PASSWORD_LENGTH -> {
                "Use at least $MIN_PASSWORD_LENGTH characters."
            }
            else -> null
        }
        return AuthValidationResult(emailError = emailError, passwordError = passwordError)
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

private data class AuthValidationResult(
    val displayNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
) {
    val isValid: Boolean
        get() = displayNameError == null && emailError == null && passwordError == null
}
