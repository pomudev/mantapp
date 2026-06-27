package com.mantapp.app.ui.screen.dashboard

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
import com.mantapp.app.ui.component.MantappEmptyState
import com.mantapp.app.ui.component.MantappWarningMessage
import com.mantapp.app.ui.screen.recommendation.buildRecommendationUiState
import com.mantapp.app.ui.state.DashboardUiState
import com.mantapp.app.ui.state.RecommendationAllocationUiModel
import com.mantapp.app.ui.theme.MantappCoral
import com.mantapp.app.ui.theme.MantappGold
import com.mantapp.app.ui.theme.MantappIndigo
import com.mantapp.app.ui.theme.MantappMint
import com.mantapp.app.ui.theme.MantappTheme
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onEnterCashFlowClick: () -> Unit,
    onTrackProgressClick: () -> Unit,
    onRewardsClick: () -> Unit,
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
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = MantappIndigo,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Your monthly status, recommendation, progress, and points in one place.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state.missingInputs.isNotEmpty()) {
            MissingDashboardState(
                missingInputs = state.missingInputs,
                onEnterCashFlowClick = onEnterCashFlowClick,
            )
            return@Column
        }

        CashFlowSummary(state = state)

        if (state.isLowOrNegativeDisposableIncome) {
            Spacer(modifier = Modifier.height(12.dp))
            MantappWarningMessage(
                title = "Low monthly buffer",
                message = "Mantapp treats RM 1,500 or less as low disposable income for a single-person profile. Keep essentials, debt minimums, and spending room visible before pushing savings targets.",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        AllocationSummary(items = state.allocationItems)
        Spacer(modifier = Modifier.height(16.dp))
        ProgressAndPoints(
            state = state,
            onTrackProgressClick = onTrackProgressClick,
            onRewardsClick = onRewardsClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
        NextActionCard(action = state.nextRecommendedAction, onTrackProgressClick = onTrackProgressClick)
    }
}

@Composable
private fun MissingDashboardState(
    missingInputs: List<String>,
    onEnterCashFlowClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        MantappEmptyState(
            title = "Dashboard needs setup",
            message = "Complete ${missingInputs.joinToString()} before Mantapp can show your monthly overview.",
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = onEnterCashFlowClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(text = "Enter Cash Flow")
        }
    }
}

@Composable
private fun CashFlowSummary(state: DashboardUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MantappMint,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Monthly status",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            MetricRow(
                left = DashboardMetric("Salary", state.monthlyIncome),
                right = DashboardMetric("Essentials", state.totalEssentialExpenses),
            )
            MetricRow(
                left = DashboardMetric("Disposable", state.disposableIncome),
                right = DashboardMetric("Points", state.pointsBalance.toString(), prefix = ""),
            )
        }
    }
}

@Composable
private fun MetricRow(
    left: DashboardMetric,
    right: DashboardMetric,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(metric = left, modifier = Modifier.weight(1f))
        MetricCard(metric = right, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MetricCard(
    metric: DashboardMetric,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = metric.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${metric.prefix}${metric.value}",
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AllocationSummary(items: List<RecommendationAllocationUiModel>) {
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
                text = "Allocation summary",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            items.forEach { item ->
                AllocationChartRow(item = item)
            }
        }
    }
}

@Composable
private fun AllocationChartRow(item: RecommendationAllocationUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${item.percentage}% | RM ${item.amount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MantappIndigo,
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
    }
}

@Composable
private fun ProgressAndPoints(
    state: DashboardUiState,
    onTrackProgressClick: () -> Unit,
    onRewardsClick: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = state.progressTitle, style = MaterialTheme.typography.bodyMedium)
                Text(text = state.progressMessage, style = MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick = onTrackProgressClick, shape = RoundedCornerShape(14.dp)) {
                    Text(text = "Track")
                }
            }
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Rewards",
                    style = MaterialTheme.typography.titleMedium,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${state.pointsBalance} points",
                    style = MaterialTheme.typography.titleLarge,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = "Points appear after approved progress actions.", style = MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick = onRewardsClick, shape = RoundedCornerShape(14.dp)) {
                    Text(text = "Rewards")
                }
            }
        }
    }
}

@Composable
private fun NextActionCard(
    action: String,
    onTrackProgressClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MantappGold.copy(alpha = 0.24f),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Next action",
                style = MaterialTheme.typography.titleLarge,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = action, style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = onTrackProgressClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = "Log Progress")
            }
        }
    }
}

fun buildDashboardUiState(
    monthlyIncome: String?,
    totalEssentialExpenses: String?,
    disposableIncome: String?,
    profileComplete: Boolean,
): DashboardUiState {
    val recommendationState = buildRecommendationUiState(
        monthlyIncome = monthlyIncome,
        totalEssentialExpenses = totalEssentialExpenses,
        disposableIncome = disposableIncome,
        profileComplete = profileComplete,
    )

    val missingInputs = recommendationState.missingInputs
    if (missingInputs.isNotEmpty()) {
        return DashboardUiState(missingInputs = missingInputs)
    }

    val disposable = disposableIncome.toMoneyOrZero()
    val nextAction = when {
        disposable <= BigDecimal.ZERO -> "Review essentials and look for one bill or commitment to reduce this month."
        disposable <= BigDecimal("1500.00") -> "Choose one emergency-savings or debt-minimum action to track first."
        else -> "Pick the highest-priority allocation and log your first progress action."
    }

    return DashboardUiState(
        monthlyIncome = monthlyIncome.orEmpty(),
        totalEssentialExpenses = totalEssentialExpenses.orEmpty(),
        disposableIncome = disposableIncome.orEmpty(),
        allocationItems = recommendationState.allocationItems,
        isLowOrNegativeDisposableIncome = disposable <= BigDecimal("1500.00"),
        nextRecommendedAction = nextAction,
    )
}

private data class DashboardMetric(
    val label: String,
    val value: String,
    val prefix: String = "RM ",
)

private fun String?.toMoneyOrZero(): BigDecimal {
    return runCatching {
        BigDecimal(this ?: "0").setScale(2, RoundingMode.HALF_UP)
    }.getOrDefault(BigDecimal.ZERO)
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MantappTheme {
        DashboardScreen(
            state = buildDashboardUiState(
                monthlyIncome = "3200.00",
                totalEssentialExpenses = "1700.00",
                disposableIncome = "1500.00",
                profileComplete = true,
            ),
            onEnterCashFlowClick = {},
            onTrackProgressClick = {},
            onRewardsClick = {},
        )
    }
}
