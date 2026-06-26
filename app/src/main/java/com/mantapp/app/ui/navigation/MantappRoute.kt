package com.mantapp.app.ui.navigation

sealed interface MantappRoute {
    val route: String

    data object Login : MantappRoute {
        override val route = "login"
    }

    data object Register : MantappRoute {
        override val route = "register"
    }

    data object Onboarding : MantappRoute {
        override val route = "onboarding"
    }

    data object IncomeExpense : MantappRoute {
        override val route = "income_expense"
    }

    data object Recommendation : MantappRoute {
        private const val BASE_ROUTE = "recommendation"
        const val MONTHLY_INCOME_ARG = "monthlyIncome"
        const val TOTAL_EXPENSES_ARG = "totalExpenses"
        const val DISPOSABLE_INCOME_ARG = "disposableIncome"
        const val PROFILE_COMPLETE_ARG = "profileComplete"

        override val route = "$BASE_ROUTE?$MONTHLY_INCOME_ARG={$MONTHLY_INCOME_ARG}" +
            "&$TOTAL_EXPENSES_ARG={$TOTAL_EXPENSES_ARG}" +
            "&$DISPOSABLE_INCOME_ARG={$DISPOSABLE_INCOME_ARG}" +
            "&$PROFILE_COMPLETE_ARG={$PROFILE_COMPLETE_ARG}"

        fun createRoute(
            monthlyIncome: String,
            totalExpenses: String,
            disposableIncome: String,
            profileComplete: Boolean = true,
        ): String {
            return "$BASE_ROUTE?$MONTHLY_INCOME_ARG=$monthlyIncome" +
                "&$TOTAL_EXPENSES_ARG=$totalExpenses" +
                "&$DISPOSABLE_INCOME_ARG=$disposableIncome" +
                "&$PROFILE_COMPLETE_ARG=$profileComplete"
        }
    }

    data object Dashboard : MantappRoute {
        override val route = "dashboard"
    }

    data object Progress : MantappRoute {
        override val route = "progress"
    }

    data object Rewards : MantappRoute {
        override val route = "rewards"
    }

    data object Settings : MantappRoute {
        override val route = "settings"
    }
}
