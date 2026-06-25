package com.mantapp.app.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mantapp.app.ui.component.MantappErrorState
import com.mantapp.app.ui.event.OnboardingEvent
import com.mantapp.app.ui.state.OnboardingUiState
import com.mantapp.app.ui.state.ScreenStatus
import com.mantapp.app.ui.theme.MantappGold
import com.mantapp.app.ui.theme.MantappIndigo
import com.mantapp.app.ui.theme.MantappMint
import com.mantapp.app.ui.theme.MantappTheme

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onComplete()
        }
    }

    val activeSteps = onboardingSteps.filterNot { it.key in state.skippedQuestionKeys }
    val currentStep = activeSteps[state.currentStepIndex.coerceIn(activeSteps.indices)]
    val progress = (state.currentStepIndex + 1).toFloat() / activeSteps.size.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Money Setup",
            style = MaterialTheme.typography.headlineLarge,
            color = MantappIndigo,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "A few quick answers help Mantapp shape your first plan.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MantappMint,
        )
        Text(
            text = "Step ${state.currentStepIndex + 1} of ${activeSteps.size}",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = currentStep.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = currentStep.subtitle,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(20.dp))
                StepInput(
                    step = currentStep,
                    state = state,
                    onEvent = onEvent,
                )
            }
        }

        state.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            MantappErrorState(title = "One more detail", message = message)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { onEvent(OnboardingEvent.Back) },
                modifier = Modifier.weight(1f),
                enabled = state.currentStepIndex > 0,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(text = "Back")
            }
            Button(
                onClick = {
                    if (currentStep.type == OnboardingStepType.Review) {
                        onEvent(OnboardingEvent.SubmitProfile)
                    } else {
                        onEvent(OnboardingEvent.Continue)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = if (currentStep.type == OnboardingStepType.Review) "Finish" else "Next")
            }
        }
    }
}

@Composable
private fun StepInput(
    step: OnboardingStep,
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
) {
    val answer = state.answers[step.key].orEmpty()
    when (step.type) {
        OnboardingStepType.Money -> {
            OutlinedTextField(
                value = answer,
                onValueChange = { input ->
                    onEvent(
                        OnboardingEvent.AnswerChanged(
                            step.key,
                            input.filter { it.isDigit() || it == '.' },
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Monthly income in RM") },
                prefix = { Text(text = "RM") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
            )
        }
        OnboardingStepType.SingleChoice -> {
            ChoiceGrid(
                options = step.options,
                selected = answer,
                onSelected = { onEvent(OnboardingEvent.AnswerChanged(step.key, it)) },
            )
        }
        OnboardingStepType.FreeText -> {
            OutlinedTextField(
                value = answer,
                onValueChange = { onEvent(OnboardingEvent.AnswerChanged(step.key, it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = step.inputLabel) },
                minLines = 3,
                shape = RoundedCornerShape(14.dp),
            )
        }
        OnboardingStepType.Review -> ReviewAnswers(state = state)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ChoiceGrid(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            if (isSelected) {
                Button(
                    onClick = { onSelected(option) },
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(text = option)
                }
            } else {
                OutlinedButton(
                    onClick = { onSelected(option) },
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
private fun ReviewAnswers(state: OnboardingUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        onboardingSteps
            .filterNot { it.type == OnboardingStepType.Review || it.key in state.skippedQuestionKeys }
            .forEach { step ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (step.key == "main_financial_goals") {
                        MantappGold.copy(alpha = 0.16f)
                    } else {
                        MantappMint.copy(alpha = 0.72f)
                    },
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = step.reviewLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MantappIndigo,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.answers[step.key].orEmpty(),
                            modifier = Modifier.padding(top = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
    }
}

private enum class OnboardingStepType {
    Money,
    SingleChoice,
    FreeText,
    Review,
}

private data class OnboardingStep(
    val key: String,
    val title: String,
    val subtitle: String,
    val type: OnboardingStepType,
    val options: List<String> = emptyList(),
    val inputLabel: String = "",
    val reviewLabel: String = title,
)

private val onboardingSteps = listOf(
    OnboardingStep(
        key = "monthly_income",
        title = "What is your monthly income?",
        subtitle = "Use your usual take-home income so the plan stays realistic.",
        type = OnboardingStepType.Money,
        reviewLabel = "Monthly income",
    ),
    OnboardingStep(
        key = "employment_status",
        title = "What best describes your work right now?",
        subtitle = "This helps us understand how predictable your cash flow may be.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("Student", "Fresh graduate", "Full-time", "Part-time", "Freelance", "Not working"),
        reviewLabel = "Employment",
    ),
    OnboardingStep(
        key = "income_stability",
        title = "How steady is your income?",
        subtitle = "No judgment here. Variable income just needs a different buffer.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("Very steady", "Mostly steady", "Changes monthly", "Irregular"),
        reviewLabel = "Income stability",
    ),
    OnboardingStep(
        key = "debt_status",
        title = "Do you have debt to manage?",
        subtitle = "Include credit cards, PTPTN, personal loans, or buy-now-pay-later balances.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("No debt right now", "Some manageable debt", "High-interest debt", "Behind on payments"),
        reviewLabel = "Debt status",
    ),
    OnboardingStep(
        key = "debt_types",
        title = "What type of debt should we consider first?",
        subtitle = "Pick the one that feels most important for your first plan.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("Credit card", "PTPTN", "Personal loan", "Car loan", "BNPL", "Other"),
        reviewLabel = "Debt type",
    ),
    OnboardingStep(
        key = "emergency_savings_status",
        title = "Do you have emergency savings?",
        subtitle = "A cash buffer helps protect your plan from surprise expenses.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("None yet", "A little", "Around 1 month", "3 months or more"),
        reviewLabel = "Emergency savings",
    ),
    OnboardingStep(
        key = "emergency_savings_coverage",
        title = "How many months could your savings cover?",
        subtitle = "Estimate using essentials like rent, food, transport, bills, and debt minimums.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("0 months", "Less than 1 month", "1-2 months", "3-5 months", "6+ months"),
        reviewLabel = "Savings coverage",
    ),
    OnboardingStep(
        key = "main_financial_goals",
        title = "What is your main money goal?",
        subtitle = "Choose the goal you want Mantapp to prioritize first.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("Build savings", "Pay down debt", "Save for a purchase", "Invest later", "Spend with control"),
        reviewLabel = "Main goal",
    ),
    OnboardingStep(
        key = "short_term_purchase_goal",
        title = "Any short-term purchase coming up?",
        subtitle = "Think laptop, phone, travel, moving costs, or course fees.",
        type = OnboardingStepType.FreeText,
        inputLabel = "Upcoming purchase or goal",
        reviewLabel = "Short-term purchase",
    ),
    OnboardingStep(
        key = "risk_tolerance",
        title = "How do you feel about financial risk?",
        subtitle = "For now this only shapes educational guidance, not investment advice.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("Very cautious", "Balanced", "Open to learning", "Comfortable with risk"),
        reviewLabel = "Risk comfort",
    ),
    OnboardingStep(
        key = "budgeting_preference",
        title = "What budgeting style fits you?",
        subtitle = "Pick the style you are most likely to stick with.",
        type = OnboardingStepType.SingleChoice,
        options = listOf("Simple rules", "Detailed categories", "Weekly check-ins", "Flexible targets"),
        reviewLabel = "Budgeting style",
    ),
    OnboardingStep(
        key = "upcoming_major_expenses",
        title = "Any major expenses soon?",
        subtitle = "Tell Mantapp about anything that could affect this month's plan.",
        type = OnboardingStepType.FreeText,
        inputLabel = "Major expenses",
        reviewLabel = "Major expenses",
    ),
    OnboardingStep(
        key = "review",
        title = "Review your setup",
        subtitle = "Check your answers before continuing to income and expenses.",
        type = OnboardingStepType.Review,
    ),
)

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    MantappTheme {
        OnboardingScreen(
            state = OnboardingUiState(
                answers = mapOf("monthly_income" to "3200"),
            ),
            onEvent = {},
            onComplete = {},
        )
    }
}
