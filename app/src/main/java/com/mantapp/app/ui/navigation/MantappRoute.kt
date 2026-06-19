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
        override val route = "recommendation"
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
