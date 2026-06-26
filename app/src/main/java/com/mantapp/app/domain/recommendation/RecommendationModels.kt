package com.mantapp.app.domain.recommendation

import java.math.BigDecimal
import java.math.RoundingMode

data class RecommendationInput(
    val monthlyIncome: BigDecimal,
    val totalEssentialExpenses: BigDecimal,
    val disposableIncome: BigDecimal,
    val profileComplete: Boolean,
)

data class RuleGuidanceSignal(
    val title: String,
    val message: String,
    val priorityAdjustmentPercent: Int? = null,
)

data class RecommendationGuidance(
    val signals: List<RuleGuidanceSignal>,
    val safetyNotes: List<String>,
)

data class RecommendationAllocation(
    val label: String,
    val percentage: Int,
    val amount: BigDecimal,
    val note: String,
)

enum class RecommendationDecisionSource {
    AiFinal,
    LocalFallback,
}

data class RecommendationResult(
    val source: RecommendationDecisionSource,
    val allocations: List<RecommendationAllocation>,
    val rationale: String,
    val aiExplanation: String? = null,
)

interface AiRecommendationProvider {
    fun generateFinalRecommendation(
        input: RecommendationInput,
        guidance: RecommendationGuidance,
    ): RecommendationResult
}

class RecommendationCoordinator(
    private val guidanceEngine: RuleGuidanceEngine = RuleGuidanceEngine(),
    private val fallbackProvider: LocalFallbackRecommendationProvider = LocalFallbackRecommendationProvider(),
    private val aiProvider: AiRecommendationProvider? = null,
    private val aiEnabled: Boolean = false,
) {
    fun recommend(input: RecommendationInput): Pair<RecommendationGuidance, RecommendationResult> {
        val guidance = guidanceEngine.buildGuidance(input)
        val result = if (aiEnabled && aiProvider != null) {
            aiProvider.generateFinalRecommendation(input = input, guidance = guidance)
        } else {
            fallbackProvider.generateFallback(input = input, guidance = guidance)
        }

        return guidance to result
    }
}

class RuleGuidanceEngine {
    fun buildGuidance(input: RecommendationInput): RecommendationGuidance {
        val signals = buildList {
            when {
                input.disposableIncome <= BigDecimal.ZERO -> add(
                    RuleGuidanceSignal(
                        title = "Cash flow pressure",
                        message = "Essentials are equal to or higher than salary. Guide the final recommendation toward expense review and away from aggressive savings targets.",
                    ),
                )
                input.disposableIncome <= LOW_DISPOSABLE_INCOME_THRESHOLD -> add(
                    RuleGuidanceSignal(
                        title = "Low disposable income",
                        message = "Disposable income is RM 1,500 or less for a single-person profile. Increase priority for emergency savings, debt minimums, and flexible spending.",
                        priorityAdjustmentPercent = 20,
                    ),
                )
                else -> add(
                    RuleGuidanceSignal(
                        title = "Stable monthly buffer",
                        message = "Disposable income is above the low-buffer threshold. Balance emergency savings, debt progress, long-term preparation, and flexible spending.",
                    ),
                )
            }
        }

        return RecommendationGuidance(
            signals = signals,
            safetyNotes = listOf(
                "Keep recommendations educational and avoid presenting them as licensed financial, investment, tax, or legal advice.",
                "Preserve a realistic flexible-spending floor where possible.",
            ),
        )
    }

    private companion object {
        val LOW_DISPOSABLE_INCOME_THRESHOLD: BigDecimal = BigDecimal("1500.00")
    }
}

class LocalFallbackRecommendationProvider {
    fun generateFallback(
        input: RecommendationInput,
        guidance: RecommendationGuidance,
    ): RecommendationResult {
        val allocationRules = when {
            input.disposableIncome <= BigDecimal.ZERO -> emptyList()
            input.disposableIncome <= LOW_DISPOSABLE_INCOME_THRESHOLD -> listOf(
                FallbackAllocationRule("Emergency savings", 40, "Build a small buffer before taking bigger steps."),
                FallbackAllocationRule("Debt repayment", 20, "Keep minimums and urgent balances moving."),
                FallbackAllocationRule("Long-term savings", 0, "Pause long-term investing until the monthly buffer improves."),
                FallbackAllocationRule("Flexible spending", 40, "Leave enough room for real monthly life."),
            )
            else -> listOf(
                FallbackAllocationRule("Emergency savings", 25, "Keep strengthening your cash buffer or near-term goal."),
                FallbackAllocationRule("Debt repayment", 20, "Continue reducing debt without crowding out essentials."),
                FallbackAllocationRule("Long-term savings", 25, "Prepare for retirement or investment learning once basics are covered."),
                FallbackAllocationRule("Flexible spending", 30, "Keep a realistic amount for variable spending."),
            )
        }

        return RecommendationResult(
            source = RecommendationDecisionSource.LocalFallback,
            allocations = allocationRules.map { rule ->
                RecommendationAllocation(
                    label = rule.label,
                    percentage = rule.percentage,
                    amount = input.disposableIncome.percentage(rule.percentage),
                    note = rule.note,
                )
            },
            rationale = fallbackRationale(input = input, guidance = guidance),
        )
    }

    private fun fallbackRationale(
        input: RecommendationInput,
        guidance: RecommendationGuidance,
    ): String {
        val signalSummary = guidance.signals.firstOrNull()?.message.orEmpty()
        return when {
            input.disposableIncome <= BigDecimal.ZERO ->
                "AI final judgment is not enabled yet. Local fallback uses rule guidance to avoid savings targets until expenses are reviewed. $signalSummary"
            input.disposableIncome <= LOW_DISPOSABLE_INCOME_THRESHOLD ->
                "AI final judgment is not enabled yet. Local fallback uses rule guidance for a low monthly buffer and prioritizes emergency savings, debt minimums, and breathing room."
            else ->
                "AI final judgment is not enabled yet. Local fallback uses rule guidance to balance savings, debt progress, long-term preparation, and flexible spending."
        }
    }

    private companion object {
        val LOW_DISPOSABLE_INCOME_THRESHOLD: BigDecimal = BigDecimal("1500.00")
    }
}

private data class FallbackAllocationRule(
    val label: String,
    val percentage: Int,
    val note: String,
)

private fun BigDecimal.percentage(value: Int): BigDecimal {
    return multiply(BigDecimal(value)).divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
}
