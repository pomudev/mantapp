package com.mantapp.app.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mantapp.app.ui.component.MantappErrorState
import com.mantapp.app.ui.component.MantappSuccessMessage
import com.mantapp.app.ui.component.MantappTextField
import com.mantapp.app.ui.event.AuthEvent
import com.mantapp.app.ui.state.AuthDestination
import com.mantapp.app.ui.state.AuthUiState
import com.mantapp.app.ui.state.ScreenStatus
import com.mantapp.app.ui.theme.MantappGold
import com.mantapp.app.ui.theme.MantappIndigo
import com.mantapp.app.ui.theme.MantappMint
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
        BrandHeader(
            title = "Mantapp",
            subtitle = "Build better money habits, one step at a time.",
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Log In",
            style = MaterialTheme.typography.headlineMedium,
            color = MantappIndigo,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Welcome back. Your local finance plan is ready.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LocalOnlyNote()
        Spacer(modifier = Modifier.height(20.dp))
        MantappTextField(
            value = state.email,
            onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            supportingText = state.emailError,
            isError = state.emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            supportingText = state.passwordError,
            isError = state.passwordError != null,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ReturningUserShortcut(
            checked = state.returningOnboardedUser,
            onCheckedChange = { onEvent(AuthEvent.ReturningOnboardedUserChanged(it)) },
        )
        AuthFeedback(state = state)
        Spacer(modifier = Modifier.height(20.dp))
        AuthPrimaryButton(
            text = "Log In",
            loadingText = "Logging in",
            isLoading = state.status == ScreenStatus.Loading,
            onClick = { onEvent(AuthEvent.SubmitLogin) },
        )
        AuthTextLink(
            prompt = "Don't have an account?",
            action = "Sign Up",
            onClick = onRegisterClick,
        )
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
        TextButton(onClick = onLoginClick, modifier = Modifier.align(Alignment.Start)) {
            Text(text = "< Log In")
        }
        Spacer(modifier = Modifier.height(12.dp))
        BrandHeader(
            title = "Create Account",
            subtitle = "Start with a local profile made for your goals.",
            compact = true,
        )
        Spacer(modifier = Modifier.height(28.dp))
        LocalOnlyNote()
        Spacer(modifier = Modifier.height(20.dp))
        MantappTextField(
            value = state.displayName,
            onValueChange = { onEvent(AuthEvent.DisplayNameChanged(it)) },
            label = "Full Name / Username",
            modifier = Modifier.fillMaxWidth(),
            supportingText = state.displayNameError,
            isError = state.displayNameError != null,
        )
        Spacer(modifier = Modifier.height(12.dp))
        MantappTextField(
            value = state.email,
            onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email Address",
            modifier = Modifier.fillMaxWidth(),
            supportingText = state.emailError,
            isError = state.emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            supportingText = state.passwordError ?: "Use at least 8 characters.",
            isError = state.passwordError != null,
        )
        AuthFeedback(state = state)
        Spacer(modifier = Modifier.height(20.dp))
        AuthPrimaryButton(
            text = "Create Account",
            loadingText = "Creating account",
            isLoading = state.status == ScreenStatus.Loading,
            onClick = { onEvent(AuthEvent.SubmitRegistration) },
        )
        AuthTextLink(
            prompt = "Already have an account?",
            action = "Log In",
            onClick = onLoginClick,
        )
    }
}

@Composable
private fun AuthScaffold(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        content = content,
    )
}

@Composable
private fun BrandHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(if (compact) 36.dp else 44.dp),
                shape = RoundedCornerShape(14.dp),
                color = MantappMint,
                contentColor = MantappIndigo,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "M",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp),
                style = if (compact) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.headlineLarge
                },
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = subtitle,
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    supportingText: String?,
    isError: Boolean,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        supportingText = supportingText?.let { { Text(text = it) } },
        isError = isError,
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                Text(text = if (passwordVisible) "Hide" else "Show")
            }
        },
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun ReturningUserShortcut(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "I finished setup on this device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MantappIndigo,
                )
                Text(
                    text = "Skip onboarding after the mock login.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun AuthPrimaryButton(
    text: String,
    loadingText: String,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = loadingText,
                modifier = Modifier.padding(start = 10.dp),
                fontWeight = FontWeight.SemiBold,
            )
        } else {
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AuthTextLink(
    prompt: String,
    action: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = prompt, style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onClick) {
            Text(text = action, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LocalOnlyNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MantappGold.copy(alpha = 0.18f),
        contentColor = MantappIndigo,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Local-only preview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Your mock account stays on this device for now.",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AuthFeedback(state: AuthUiState) {
    state.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        MantappErrorState(title = "Almost there", message = message)
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
