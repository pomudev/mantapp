package com.mantapp.app.data.local

import com.mantapp.app.data.local.entity.RewardEntity

fun defaultRewardEntities(): List<RewardEntity> {
    return listOf(
        RewardEntity(
            id = "familymart-rm5",
            merchant = "FamilyMart",
            title = "FamilyMart RM5 Voucher",
            pointCost = 500,
            isAvailable = true,
        ),
        RewardEntity(
            id = "tgv-rm5",
            merchant = "TGV",
            title = "TGV Movie Ticket RM5 Voucher",
            pointCost = 500,
            isAvailable = true,
        ),
        RewardEntity(
            id = "gsc-snack-combo",
            merchant = "GSC",
            title = "GSC Snack Combo Voucher",
            pointCost = 800,
            isAvailable = true,
        ),
        RewardEntity(
            id = "petronas-rm5",
            merchant = "Petronas",
            title = "Petronas RM5 Voucher",
            pointCost = 1200,
            isAvailable = true,
        ),
    )
}
