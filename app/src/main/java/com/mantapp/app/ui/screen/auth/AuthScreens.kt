package com.mantapp.app.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mantapp.app.ui.component.MantappErrorState
import com.mantapp.app.ui.component.MantappLoadingState
import com.mantapp.app.ui.component.MantappSuccessMessage
import com.mantapp.app.ui.component.MantappTextField
import com.mantapp.app.ui.component.MantappWarningMessage
import com.mantapp.app.ui.event.AuthEvent
import com.mantapp.app.ui.state.AuthDestination
import com.mantapp.app.ui.state.AuthUiState
import com.mantapp.app.ui.state.ScreenStatus
import com.mantapp.app.ui.theme.MantappTheme

@Composable
fun LoginScreen(
    state: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    onRegisterClick: () -> Unit,
    onAuthenticated: (AuthDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.destination) {
        state.destination?.let { destination ->
            onAuthenticated(destination)
            onEvent(AuthEvent.NavigationHandled)
        }
    }

    AuthScaffold(modifier = modifier) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Access the finance plan stored on this device.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LocalOnlyMessage()
        Spacer(modifier = Modifier.height(16.dp))
        MantappTextField(
            value = state.email,
            onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = state.returningOnboardedUser,
                onCheckedChange = { onEvent(AuthEvent.ReturningOnboardedUserChanged(it)) },
            )
            Text(
                text = "Onboarding completed on this device",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        AuthFeedback(state = state)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onEvent(AuthEvent.SubmitLogin) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.status != ScreenStatus.Loading,
        ) {
            Text(text = "Login")
        }
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Create account")
        }
    }
}

@Composable
fun RegistrationScreen(
    state: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    onLoginClick: () -> Unit,
    onRegistered: (AuthDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.destination) {
        state.destination?.let { destination ->
            onRegistered(destination)
            onEvent(AuthEvent.NavigationHandled)
        }
    }

    AuthScaffold(modifier = modifier) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Set up a local Mantapp profile for this device.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LocalOnlyMessage()
        Spacer(modifier = Modifier.height(16.dp))
        MantappTextField(
            value = state.displayName,
            onValueChange = { onEvent(AuthEvent.DisplayNameChanged(it)) },
            label = "Name",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        MantappTextField(
            value = state.email,
            onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.confirmPassword,
            onValueChange = { onEvent(AuthEvent.ConfirmPasswordChanged(it)) },
            label = "Confirm password",
        )
        AuthFeedback(state = state)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onEvent(AuthEvent.SubmitRegistration) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.status != ScreenStatus.Loading,
        ) {
            Text(text = "Create account")
        }
        OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Back to login")
        }
    }
}

@Composable
private fun AuthScaffold(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )
}

@Composable
private fun LocalOnlyMessage() {
    MantappWarningMessage(
        title = "Local-only MVP",
        message = "Account access is simulated and stored offline for this prototype.",
    )
}

@Composable
private fun AuthFeedback(state: AuthUiState) {
    if (state.status == ScreenStatus.Loading) {
        Spacer(modifier = Modifier.height(16.dp))
        MantappLoadingState(label = "Checking local profile")
    }
    state.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        MantappErrorState(title = "Check your details", message = message)
    }
    state.successMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        MantappSuccessMessage(title = "Ready", message = message)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MantappTheme {
        LoginScreen(
            state = AuthUiState(email = "user@example.com"),
            onEvent = {},
            onRegisterClick = {},
            onAuthenticated = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenPreview() {
    MantappTheme {
        RegistrationScreen(
            state = AuthUiState(displayName = "Aana", email = "user@example.com"),
            onEvent = {},
            onLoginClick = {},
            onRegistered = {},
        )
    }
}
