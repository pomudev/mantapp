package com.mantapp.app.data.local

import com.mantapp.app.domain.model.FinancialProfile
import com.mantapp.app.domain.model.MonthlyFinance
import com.mantapp.app.domain.model.PointTransaction
import com.mantapp.app.domain.model.ProgressLog
import com.mantapp.app.domain.model.RewardCatalogueItem
import com.mantapp.app.domain.model.SavedRecommendation
import com.mantapp.app.domain.model.SessionState
import com.mantapp.app.domain.model.UserAccount
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class LocalMantappStore @Inject constructor() {
    val session = MutableStateFlow(SessionState())
    val users = MutableStateFlow<List<UserAccount>>(emptyList())
    val profiles = MutableStateFlow<Map<String, FinancialProfile>>(emptyMap())
    val monthlyFinances = MutableStateFlow<Map<String, MonthlyFinance>>(emptyMap())
    val recommendations = MutableStateFlow<List<SavedRecommendation>>(emptyList())
    val progressLogs = MutableStateFlow<List<ProgressLog>>(emptyList())
    val rewards = MutableStateFlow(defaultRewards())
    val pointTransactions = MutableStateFlow<List<PointTransaction>>(emptyList())
}

private fun defaultRewards(): List<RewardCatalogueItem> {
    return listOf(
        RewardCatalogueItem(
            id = "familymart-rm5",
            merchant = "FamilyMart",
            title = "FamilyMart RM5 Voucher",
            pointCost = 500,
        ),
        RewardCatalogueItem(
            id = "tgv-rm5",
            merchant = "TGV",
            title = "TGV Movie Ticket RM5 Voucher",
            pointCost = 500,
        ),
        RewardCatalogueItem(
            id = "gsc-snack-combo",
            merchant = "GSC",
            title = "GSC Snack Combo Voucher",
            pointCost = 800,
        ),
        RewardCatalogueItem(
            id = "petronas-rm5",
            merchant = "Petronas",
            title = "Petronas RM5 Voucher",
            pointCost = 1200,
        ),
    )
}
