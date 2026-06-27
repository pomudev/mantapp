package com.mantapp.app.ui.screen.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mantapp.app.ui.component.MantappEmptyState
import com.mantapp.app.ui.component.MantappErrorState
import com.mantapp.app.ui.component.MantappMoneyField
import com.mantapp.app.ui.component.MantappSuccessMessage
import com.mantapp.app.ui.component.MantappWarningMessage
import com.mantapp.app.ui.event.ProgressEvent
import com.mantapp.app.ui.state.ProgressActionType
import com.mantapp.app.ui.state.ProgressLogUiModel
import com.mantapp.app.ui.state.ProgressUiState
import com.mantapp.app.ui.theme.MantappCoral
import com.mantapp.app.ui.theme.MantappGold
import com.mantapp.app.ui.theme.MantappIndigo
import com.mantapp.app.ui.theme.MantappMint
import com.mantapp.app.ui.theme.MantappTheme
import com.mantapp.app.viewmodel.displayTitle

@Composable
fun ProgressScreen(
    state: ProgressUiState,
    onEvent: (ProgressEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineLarge,
            color = MantappIndigo,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Track the actions you take against your current monthly plan.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))

        MonthlyStatus(state = state)
        Spacer(modifier = Modifier.height(16.dp))
        ProgressEntryCard(state = state, onEvent = onEvent)
        ProgressFeedback(state = state)
        Spacer(modifier = Modifier.height(16.dp))
        ProgressLogList(logs = state.logs)
        Spacer(modifier = Modifier.height(16.dp))
        StreakCandidateCard(state = state)
    }
}

@Composable
private fun MonthlyStatus(state: ProgressUiState) {
    val target = state.monthlyTargetCount.coerceAtLeast(1)
    val progress = state.monthlyCompletionCount.toFloat() / target.toFloat()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MantappMint,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Monthly completion",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${state.monthlyCompletionCount} of $target actions logged",
                style = MaterialTheme.typography.bodyMedium,
                color = MantappIndigo,
            )
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
            )
            Text(
                text = "Linked to: ${state.activeRecommendationLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MantappIndigo,
            )
        }
    }
}

@Composable
private fun ProgressEntryCard(
    state: ProgressUiState,
    onEvent: (ProgressEvent) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Log progress",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            ActionTypeSelector(
                selected = state.selectedActionType,
                onSelected = { onEvent(ProgressEvent.ActionTypeChanged(it)) },
            )
            if (state.selectedActionType != ProgressActionType.WeeklyCheckIn) {
                MantappMoneyField(
                    value = state.amount,
                    onValueChange = { onEvent(ProgressEvent.AmountChanged(it)) },
                    label = "Completed amount in RM",
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = state.amountError,
                    isError = state.amountError != null,
                )
            }
            OutlinedTextField(
                value = state.note,
                onValueChange = { onEvent(ProgressEvent.NoteChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Progress note") },
                supportingText = state.noteError?.let { { Text(text = it) } },
                isError = state.noteError != null,
                minLines = 3,
                shape = RoundedCornerShape(14.dp),
            )
            Button(
                onClick = { onEvent(ProgressEvent.SubmitProgressLog) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = "Add Progress")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ActionTypeSelector(
    selected: ProgressActionType,
    onSelected: (ProgressActionType) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ProgressActionType.entries.forEach { actionType ->
            if (actionType == selected) {
                Button(onClick = { onSelected(actionType) }, shape = RoundedCornerShape(14.dp)) {
                    Text(text = actionType.displayTitle())
                }
            } else {
                OutlinedButton(onClick = { onSelected(actionType) }, shape = RoundedCornerShape(14.dp)) {
                    Text(text = actionType.displayTitle())
                }
            }
        }
    }
}

@Composable
private fun ProgressFeedback(state: ProgressUiState) {
    state.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(12.dp))
        MantappErrorState(title = "Progress needs a little more detail", message = message)
    }
    state.successMessage?.let { message ->
        Spacer(modifier = Modifier.height(12.dp))
        MantappSuccessMessage(title = "Progress logged", message = message)
    }
}

@Composable
private fun ProgressLogList(logs: List<ProgressLogUiModel>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "This month's logs",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            if (logs.isEmpty()) {
                MantappEmptyState(
                    title = "No progress logged yet",
                    message = "Add a weekly check-in, savings action, debt repayment, or completed allocation.",
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                logs.forEach { log -> ProgressLogRow(log = log) }
            }
        }
    }
}

@Composable
private fun ProgressLogRow(log: ProgressLogUiModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = when (log.actionType) {
            ProgressActionType.WeeklyCheckIn -> MantappMint.copy(alpha = 0.72f)
            ProgressActionType.SavingsAction -> MantappGold.copy(alpha = 0.22f)
            ProgressActionType.DebtRepayment -> MantappCoral.copy(alpha = 0.16f)
            ProgressActionType.AllocationComplete -> MaterialTheme.colorScheme.primaryContainer
        },
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = log.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = log.completionStatus,
                    style = MaterialTheme.typography.labelMedium,
                    color = MantappIndigo,
                )
            }
            if (log.amount.isNotBlank()) {
                Text(text = "RM ${log.amount}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = log.note, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Linked to ${log.linkedRecommendationLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun StreakCandidateCard(state: ProgressUiState) {
    MantappWarningMessage(
        title = "Streak candidate",
        message = "${state.streakCandidateWeeks} weekly check-ins logged. Streak rewards will be calculated once reward logic is connected.",
    )
}

@Preview(showBackground = true)
@Composable
private fun ProgressScreenPreview() {
    MantappTheme {
        ProgressScreen(
            state = ProgressUiState(
                logs = listOf(
                    ProgressLogUiModel(
                        id = "progress-1",
                        actionType = ProgressActionType.SavingsAction,
                        title = "Savings action",
                        amount = "150.00",
                        note = "Moved money to emergency savings.",
                        linkedRecommendationLabel = "Current fallback allocation",
                        completionStatus = "Logged",
                    ),
                ),
                monthlyCompletionCount = 1,
            ),
            onEvent = {},
        )
    }
}
