package com.mantapp.app.ui.screen.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mantapp.app.domain.recommendation.RecommendationCoordinator
import com.mantapp.app.domain.recommendation.RecommendationDecisionSource
import com.mantapp.app.domain.recommendation.RecommendationInput
import com.mantapp.app.ui.component.MantappEmptyState
import com.mantapp.app.ui.component.MantappWarningMessage
import com.mantapp.app.ui.state.RecommendationAllocationUiModel
import com.mantapp.app.ui.state.RecommendationDecisionSourceUi
import com.mantapp.app.ui.state.RecommendationGuidanceUiModel
import com.mantapp.app.ui.state.RecommendationUiState
import com.mantapp.app.ui.theme.MantappCoral
import com.mantapp.app.ui.theme.MantappGold
import com.mantapp.app.ui.theme.MantappIndigo
import com.mantapp.app.ui.theme.MantappMint
import com.mantapp.app.ui.theme.MantappTheme
import java.math.BigDecimal
import java.math.RoundingMode

private const val FINANCIAL_DISCLAIMER =
    "Mantapp provides general budgeting guidance based on the information entered by the user. " +
        "Recommendations are for educational purposes only and should not be considered professional " +
        "financial, investment, or legal advice. Users should consult a qualified financial advisor " +
        "before making major financial decisions."

@Composable
fun RecommendationScreen(
    state: RecommendationUiState,
    onDashboardClick: () -> Unit,
    onProgressClick: () -> Unit,
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
            text = "Recommendation",
            style = MaterialTheme.typography.headlineLarge,
            color = MantappIndigo,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = if (state.decisionSource == RecommendationDecisionSourceUi.AiFinal) {
                "AI final judgment using your profile, cash flow, and rule guidance."
            } else {
                "Local fallback output while AI final judgment is not enabled."
            },
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state.missingInputs.isNotEmpty()) {
            MissingRecommendationState(missingInputs = state.missingInputs)
            return@Column
        }

        CashFlowHeader(state = state)
        Spacer(modifier = Modifier.height(16.dp))
        DecisionSourceMessage(source = state.decisionSource)
        Spacer(modifier = Modifier.height(16.dp))
        AllocationPlan(source = state.decisionSource, items = state.allocationItems)
        Spacer(modifier = Modifier.height(16.dp))
        GuidanceCard(guidanceItems = state.guidanceItems, safetyNotes = state.safetyNotes)
        Spacer(modifier = Modifier.height(16.dp))
        RationaleCard(state = state)
        Spacer(modifier = Modifier.height(16.dp))
        MantappWarningMessage(
            title = "Educational guidance only",
            message = FINANCIAL_DISCLAIMER,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onProgressClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(text = "Track Progress")
            }
            Button(
                onClick = onDashboardClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = "Dashboard")
            }
        }
    }
}

@Composable
private fun MissingRecommendationState(missingInputs: List<String>) {
    MantappEmptyState(
        title = "Recommendation needs more details",
        message = "Complete ${missingInputs.joinToString()} before Mantapp can build your plan.",
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DecisionSourceMessage(source: RecommendationDecisionSourceUi) {
    if (source == RecommendationDecisionSourceUi.AiFinal) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MantappMint,
        ) {
            Text(
                text = "AI made the final allocation call using rule guidance and your context.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
        }
    } else {
        MantappWarningMessage(
            title = "AI final judgment not enabled",
            message = "This allocation is a local fallback generated from rule guidance. When AI integration is approved, these rules will guide the AI instead of making the final call.",
        )
    }
}

@Composable
private fun CashFlowHeader(state: RecommendationUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MantappMint,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Monthly cash flow",
                style = MaterialTheme.typography.titleMedium,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            SummaryRow(label = "Salary", amount = state.monthlyIncome)
            SummaryRow(label = "Essentials", amount = state.totalEssentialExpenses)
            SummaryRow(label = "Available to plan", amount = state.disposableIncome, highlight = true)
        }
    }
}

@Composable
private fun AllocationPlan(
    source: RecommendationDecisionSourceUi,
    items: List<RecommendationAllocationUiModel>,
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
                text = if (source == RecommendationDecisionSourceUi.AiFinal) {
                    "Final allocation"
                } else {
                    "Fallback allocation"
                },
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            items.forEach { item ->
                AllocationRow(item = item)
            }
        }
    }
}

@Composable
private fun GuidanceCard(
    guidanceItems: List<RecommendationGuidanceUiModel>,
    safetyNotes: List<String>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Rule guidance",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            guidanceItems.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MantappIndigo,
                        fontWeight = FontWeight.SemiBold,
                    )
                    item.priorityAdjustmentPercent?.let { adjustment ->
                        Text(
                            text = "Priority adjustment: +$adjustment%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MantappCoral,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Text(
                        text = item.message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            safetyNotes.forEach { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun AllocationRow(item: RecommendationAllocationUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleSmall,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${item.percentage}% | RM ${item.amount}",
                style = MaterialTheme.typography.titleSmall,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
        }
        LinearProgressIndicator(
            progress = { item.percentage / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = when (item.label) {
                "Emergency savings" -> MaterialTheme.colorScheme.primary
                "Debt repayment" -> MantappCoral
                "Long-term savings" -> MantappGold
                else -> MantappIndigo
            },
            trackColor = MantappMint,
        )
        Text(
            text = item.note,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RationaleCard(state: RecommendationUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (state.decisionSource == RecommendationDecisionSourceUi.AiFinal) {
                    "Why AI chose this"
                } else {
                    "Why this fallback appears"
                },
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = state.rationale, style = MaterialTheme.typography.bodyMedium)
            if (state.decisionSource == RecommendationDecisionSourceUi.AiFinal && state.aiExplanation != null) {
                Text(
                    text = state.aiExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MantappIndigo,
                )
            } else {
                MantappWarningMessage(
                    title = "Waiting for AI integration",
                    message = "The current output is local fallback logic. Approved AI integration will make the final allocation judgment using these rule signals and the user's full profile.",
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: String,
    highlight: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = if (highlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MantappIndigo,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
        )
        Text(
            text = "RM $amount",
            style = if (highlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MantappIndigo,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

fun buildRecommendationUiState(
    monthlyIncome: String?,
    totalEssentialExpenses: String?,
    disposableIncome: String?,
    profileComplete: Boolean,
    aiEnabled: Boolean = false,
): RecommendationUiState {
    val missingInputs = buildList {
        if (!profileComplete) add("profile setup")
        if (monthlyIncome.isNullOrBlank()) add("monthly salary")
        if (totalEssentialExpenses.isNullOrBlank()) add("essential expenses")
        if (disposableIncome.isNullOrBlank()) add("disposable income")
    }

    if (missingInputs.isNotEmpty()) {
        return RecommendationUiState(missingInputs = missingInputs)
    }

    val recommendationInput = RecommendationInput(
        monthlyIncome = monthlyIncome.toMoneyOrZero(),
        totalEssentialExpenses = totalEssentialExpenses.toMoneyOrZero(),
        disposableIncome = disposableIncome.toMoneyOrZero(),
        profileComplete = profileComplete,
    )
    val (guidance, result) = RecommendationCoordinator(aiEnabled = aiEnabled)
        .recommend(recommendationInput)

    return RecommendationUiState(
        monthlyIncome = monthlyIncome.orEmpty(),
        totalEssentialExpenses = totalEssentialExpenses.orEmpty(),
        disposableIncome = disposableIncome.orEmpty(),
        decisionSource = when (result.source) {
            RecommendationDecisionSource.AiFinal -> RecommendationDecisionSourceUi.AiFinal
            RecommendationDecisionSource.LocalFallback -> RecommendationDecisionSourceUi.LocalFallback
        },
        allocationItems = result.allocations.map { allocation ->
            RecommendationAllocationUiModel(
                label = allocation.label,
                percentage = allocation.percentage,
                amount = allocation.amount.toDisplayAmount(),
                note = allocation.note,
            )
        },
        guidanceItems = guidance.signals.map { signal ->
            RecommendationGuidanceUiModel(
                title = signal.title,
                message = signal.message,
                priorityAdjustmentPercent = signal.priorityAdjustmentPercent,
            )
        },
        safetyNotes = guidance.safetyNotes,
        rationale = result.rationale,
        aiExplanation = result.aiExplanation,
    )
}

private fun String?.toMoneyOrZero(): BigDecimal {
    return runCatching {
        BigDecimal(this ?: "0").setScale(2, RoundingMode.HALF_UP)
    }.getOrDefault(BigDecimal.ZERO)
}

private fun BigDecimal.toDisplayAmount(): String {
    return setScale(2, RoundingMode.HALF_UP).toPlainString()
}

@Preview(showBackground = true)
@Composable
private fun RecommendationScreenPreview() {
    MantappTheme {
        RecommendationScreen(
            state = buildRecommendationUiState(
                monthlyIncome = "3200.00",
                totalEssentialExpenses = "1700.00",
                disposableIncome = "1500.00",
                profileComplete = true,
            ),
            onDashboardClick = {},
            onProgressClick = {},
        )
    }
}
