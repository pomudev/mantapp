package com.mantapp.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mantapp.app.data.repository.RoomAuthRepository
import com.mantapp.app.data.repository.RoomFinancialProfileRepository
import com.mantapp.app.data.repository.RoomMoneyRepository
import com.mantapp.app.data.repository.RoomProgressRepository
import com.mantapp.app.data.repository.RoomRecommendationRepository
import com.mantapp.app.data.repository.RoomRewardRepository
import com.mantapp.app.data.security.PasswordHasher
import com.mantapp.app.domain.model.ExpenseEntry
import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.model.PointTransaction
import com.mantapp.app.domain.model.PointTransactionType
import com.mantapp.app.domain.model.ProgressActionTypeDomain
import com.mantapp.app.domain.model.ProgressLog
import com.mantapp.app.domain.model.SavedRecommendation
import com.mantapp.app.domain.model.VerificationStatus
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MantappPersistenceTest {
    private lateinit var database: MantappDatabase
    private lateinit var authRepository: RoomAuthRepository
    private lateinit var profileRepository: RoomFinancialProfileRepository
    private lateinit var moneyRepository: RoomMoneyRepository
    private lateinit var recommendationRepository: RoomRecommendationRepository
    private lateinit var progressRepository: RoomProgressRepository
    private lateinit var rewardRepository: RoomRewardRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MantappDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        seedRewards()

        authRepository = RoomAuthRepository(
            userDao = database.userDao(),
            sessionDao = database.sessionDao(),
            passwordHasher = PasswordHasher(),
        )
        profileRepository = RoomFinancialProfileRepository(database.financialProfileDao())
        moneyRepository = RoomMoneyRepository(
            database = database,
            monthlyFinanceDao = database.monthlyFinanceDao(),
            expenseDao = database.expenseDao(),
        )
        recommendationRepository = RoomRecommendationRepository(database.recommendationDao())
        progressRepository = RoomProgressRepository(database.progressLogDao())
        rewardRepository = RoomRewardRepository(
            rewardDao = database.rewardDao(),
            pointTransactionDao = database.pointTransactionDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun registerLoginAndOnboardingCompletion_persistSessionState() = runBlocking {
        val registered = authRepository.register(
            displayName = "Aana",
            email = "aana@example.com",
            password = "password123",
        )

        assertEquals(registered.id, authRepository.session.first().activeUserId)
        assertFalse(authRepository.session.first().isOnboardingComplete)
        assertNull(authRepository.login(email = "aana@example.com", password = "wrongpass"))

        authRepository.updateOnboardingComplete(isComplete = true)
        assertTrue(authRepository.session.first().isOnboardingComplete)

        authRepository.logout()
        assertNull(authRepository.session.first().activeUserId)

        val loggedIn = authRepository.login(
            email = "aana@example.com",
            password = "password123",
        )

        assertEquals(registered.id, loggedIn?.id)
        assertTrue(authRepository.session.first().isOnboardingComplete)
        assertEquals("Aana", authRepository.activeUser.first()?.displayName)
    }

    @Test
    fun financialProfile_roundTripsAnswers() = runBlocking {
        val user = createUser()
        val profile = FinancialProfile(
            userId = user.id,
            employmentStatus = "Full-time",
            incomeStability = "Very steady",
            debtStatus = "Some manageable debt",
            debtType = "PTPTN",
            emergencySavingsStatus = "A little",
            emergencySavingsCoverageMonths = "Less than 1 month",
            mainFinancialGoals = "Build savings",
            shortTermPurchaseGoal = "Laptop",
            riskTolerance = "Balanced",
            budgetingPreference = "Weekly check-ins",
            upcomingMajorExpenses = "Course fees",
            answers = mapOf(
                "employment_status" to "Full-time",
                "income_stability" to "Very steady",
                "debt_status" to "Some manageable debt",
                "debt_types" to "PTPTN",
                "emergency_savings_status" to "A little",
                "emergency_savings_coverage" to "Less than 1 month",
                "main_financial_goals" to "Build savings",
                "short_term_purchase_goal" to "Laptop",
                "risk_tolerance" to "Balanced",
                "budgeting_preference" to "Weekly check-ins",
                "upcoming_major_expenses" to "Course fees",
            ),
            completedAt = Instant.ofEpochMilli(1_000L),
        )

        profileRepository.saveProfile(profile)

        val saved = profileRepository.observeProfile(user.id).first()
        assertEquals(profile, saved)
        assertEquals("Full-time", saved?.employmentStatus)
        assertEquals("PTPTN", saved?.debtType)
        assertEquals("Less than 1 month", saved?.emergencySavingsCoverageMonths)
        assertEquals("Weekly check-ins", saved?.budgetingPreference)
    }

    @Test
    fun monthlyFinance_persistsIncomeExpensesAndDerivedTotals() = runBlocking {
        val user = createUser()
        val finance = MonthlyFinance(
            userId = user.id,
            monthlyIncome = BigDecimal("5000.00"),
            expenses = listOf(
                ExpenseEntry(categoryKey = "housing", amount = BigDecimal("1200.00")),
                ExpenseEntry(categoryKey = "groceries", amount = BigDecimal("450.50")),
            ),
            updatedAt = Instant.ofEpochMilli(2_000L),
        )

        moneyRepository.saveMonthlyFinance(finance)

        val saved = moneyRepository.observeMonthlyFinance(user.id).first()
        assertNotNull(saved)
        assertEquals(BigDecimal("5000.00"), saved?.monthlyIncome)
        assertEquals(BigDecimal("1650.50"), saved?.totalEssentialExpenses)
        assertEquals(BigDecimal("3349.50"), saved?.disposableIncome)
        assertEquals(2, saved?.expenses?.size)
    }

    @Test
    fun latestRecommendation_returnsMostRecentForUser() = runBlocking {
        val user = createUser()
        recommendationRepository.saveRecommendation(
            SavedRecommendation(
                id = "old",
                userId = user.id,
                source = "LocalFallback",
                rationale = "Old rationale",
                createdAt = Instant.ofEpochMilli(1_000L),
            ),
        )
        recommendationRepository.saveRecommendation(
            SavedRecommendation(
                id = "new",
                userId = user.id,
                source = "AiFinal",
                rationale = "New rationale",
                createdAt = Instant.ofEpochMilli(2_000L),
            ),
        )

        val latest = recommendationRepository.observeLatestRecommendation(user.id).first()
        assertEquals("new", latest?.id)
        assertEquals("New rationale", latest?.rationale)
    }

    @Test
    fun progressLogs_persistNewestFirstWithVerificationState() = runBlocking {
        val user = createUser()
        progressRepository.saveProgressLog(
            ProgressLog(
                id = "first",
                userId = user.id,
                recommendationId = null,
                actionType = ProgressActionTypeDomain.WeeklyCheckIn,
                amount = null,
                note = "Checked in",
                verificationStatus = VerificationStatus.NotRequired,
                createdAt = Instant.ofEpochMilli(1_000L),
            ),
        )
        progressRepository.saveProgressLog(
            ProgressLog(
                id = "second",
                userId = user.id,
                recommendationId = "rec-1",
                actionType = ProgressActionTypeDomain.SavingsAction,
                amount = BigDecimal("100.00"),
                note = "Saved this week",
                proofReference = "receipt.jpg",
                verificationStatus = VerificationStatus.Pending,
                createdAt = Instant.ofEpochMilli(2_000L),
            ),
        )

        val logs = progressRepository.observeProgressLogs(user.id).first()
        assertEquals(listOf("second", "first"), logs.map { it.id })
        assertEquals(VerificationStatus.Pending, logs.first().verificationStatus)
        assertEquals(BigDecimal("100.00"), logs.first().amount)
    }

    @Test
    fun rewardsAndPointTransactions_persistCatalogueAndLedger() = runBlocking {
        val user = createUser()

        val rewards = rewardRepository.observeRewardCatalogue().first()
        assertEquals(4, rewards.size)
        assertTrue(rewards.any { it.title == "FamilyMart RM5 Voucher" && it.pointCost == 500 })

        rewardRepository.savePointTransaction(
            PointTransaction(
                id = "earn-1",
                userId = user.id,
                points = 100,
                type = PointTransactionType.Earned,
                reason = "Approved savings action",
                linkedProgressLogId = "progress-1",
                linkedRewardId = null,
                createdAt = Instant.ofEpochMilli(3_000L),
            ),
        )
        rewardRepository.savePointTransaction(
            PointTransaction(
                id = "redeem-1",
                userId = user.id,
                points = -50,
                type = PointTransactionType.Redeemed,
                reason = "Simulated redemption",
                linkedProgressLogId = null,
                linkedRewardId = "familymart-rm5",
                createdAt = Instant.ofEpochMilli(4_000L),
            ),
        )

        val transactions = rewardRepository.observePointTransactions(user.id).first()
        assertEquals(listOf("redeem-1", "earn-1"), transactions.map { it.id })
        assertEquals(50, transactions.sumOf { it.points })
    }

    private suspend fun createUser() = authRepository.register(
        displayName = "Test User",
        email = "test-${System.nanoTime()}@example.com",
        password = "password123",
    )

    private fun seedRewards() {
        database.rewardDao().insertAll(defaultRewardEntities())
    }
}
