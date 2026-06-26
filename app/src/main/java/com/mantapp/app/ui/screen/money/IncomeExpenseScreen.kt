package com.mantapp.app.ui.screen.money

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mantapp.app.ui.component.MantappErrorState
import com.mantapp.app.ui.component.MantappMoneyField
import com.mantapp.app.ui.component.MantappSuccessMessage
import com.mantapp.app.ui.component.MantappWarningMessage
import com.mantapp.app.ui.event.MoneyEntryEvent
import com.mantapp.app.ui.state.MoneyEntryUiState
import com.mantapp.app.ui.state.ScreenStatus
import com.mantapp.app.ui.theme.MantappIndigo
import com.mantapp.app.ui.theme.MantappMint
import com.mantapp.app.ui.theme.MantappTheme

@Composable
fun IncomeExpenseScreen(
    state: MoneyEntryUiState,
    onEvent: (MoneyEntryEvent) -> Unit,
    onSaved: (MoneyEntryUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.status) {
        if (state.status == ScreenStatus.Success) {
            onSaved(state)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Income and Expenses",
            style = MaterialTheme.typography.headlineLarge,
            color = MantappIndigo,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Add your monthly salary and essential commitments to see what is left to plan.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Monthly salary",
                    style = MaterialTheme.typography.titleLarge,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                MantappMoneyField(
                    value = state.monthlyIncome,
                    onValueChange = { onEvent(MoneyEntryEvent.MonthlyIncomeChanged(it)) },
                    label = "Monthly salary in RM",
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = state.monthlyIncomeError,
                    isError = state.monthlyIncomeError != null,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Essential expenses",
                    style = MaterialTheme.typography.titleLarge,
                    color = MantappIndigo,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Use monthly amounts for bills and commitments you need to cover.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                expenseCategories.forEach { category ->
                    MantappMoneyField(
                        value = state.expenseInputs[category.key].orEmpty(),
                        onValueChange = {
                            onEvent(MoneyEntryEvent.ExpenseChanged(category.key, it))
                        },
                        label = category.label,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = state.expenseErrors[category.key],
                        isError = state.expenseErrors.containsKey(category.key),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        CashFlowSummary(state = state)

        if (state.isLowOrNegativeDisposableIncome && state.monthlyIncome.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            MantappWarningMessage(
                title = "Low monthly buffer",
                message = "For a single-person plan, Mantapp treats RM 1,500 or less as low disposable income. Recommendations will prioritize essentials, debt minimums, and expense review before aggressive savings.",
            )
        }

        state.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            MantappErrorState(title = "Check your inputs", message = message)
        }

        state.successMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            MantappSuccessMessage(title = "Cash flow saved", message = message)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { onEvent(MoneyEntryEvent.SubmitFinancialInputs) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(text = "Save Cash Flow")
        }
    }
}

@Composable
private fun CashFlowSummary(state: MoneyEntryUiState) {
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
                text = "Monthly preview",
                style = MaterialTheme.typography.titleMedium,
                color = MantappIndigo,
                fontWeight = FontWeight.SemiBold,
            )
            SummaryRow(label = "Total essentials", amount = state.totalEssentialExpenses)
            SummaryRow(
                label = "Disposable income",
                amount = state.disposableIncome,
                highlight = true,
            )
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

private data class ExpenseCategory(
    val key: String,
    val label: String,
)

private val expenseCategories = listOf(
    ExpenseCategory("housing", "Rent or housing"),
    ExpenseCategory("utilities", "Utilities"),
    ExpenseCategory("groceries", "Groceries"),
    ExpenseCategory("transportation", "Transportation"),
    ExpenseCategory("insurance", "Insurance"),
    ExpenseCategory("credit_card_minimum", "Credit card minimum payment"),
    ExpenseCategory("loan_repayment", "Loan repayment"),
    ExpenseCategory("phone_internet", "Phone and internet"),
    ExpenseCategory("education", "Education"),
    ExpenseCategory("subscriptions", "Subscriptions"),
    ExpenseCategory("other_commitments", "Other necessary commitments"),
)

@Preview(showBackground = true)
@Composable
private fun IncomeExpenseScreenPreview() {
    MantappTheme {
        IncomeExpenseScreen(
            state = MoneyEntryUiState(
                monthlyIncome = "3200",
                expenseInputs = mapOf(
                    "housing" to "900",
                    "groceries" to "550",
                    "transportation" to "250",
                ),
                totalEssentialExpenses = "1700.00",
                disposableIncome = "1500.00",
            ),
            onEvent = {},
            onSaved = {},
        )
    }
}
