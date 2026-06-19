package com.mantapp.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mantapp.app.ui.screen.PlaceholderScreen

@Composable
fun MantappNavGraph(
    modifier: Modifier = Modifier,
    startDestination: MantappRoute = MantappRoute.Login,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        composable(MantappRoute.Login.route) {
            PlaceholderScreen(title = "Login", subtitle = "Local-only authentication")
        }
        composable(MantappRoute.Register.route) {
            PlaceholderScreen(title = "Register", subtitle = "Create an offline Mantapp profile")
        }
        composable(MantappRoute.Onboarding.route) {
            PlaceholderScreen(title = "Onboarding", subtitle = "Financial profile questionnaire")
        }
        composable(MantappRoute.IncomeExpense.route) {
            PlaceholderScreen(title = "Income and Expenses", subtitle = "Monthly cash flow inputs")
        }
        composable(MantappRoute.Recommendation.route) {
            PlaceholderScreen(title = "Recommendation", subtitle = "Rule-based allocation plan")
        }
        composable(MantappRoute.Dashboard.route) {
            PlaceholderScreen(title = "Dashboard", subtitle = "Monthly financial overview")
        }
        composable(MantappRoute.Progress.route) {
            PlaceholderScreen(title = "Progress", subtitle = "Plan tracking and proof status")
        }
        composable(MantappRoute.Rewards.route) {
            PlaceholderScreen(title = "Rewards", subtitle = "Points and simulated vouchers")
        }
        composable(MantappRoute.Settings.route) {
            PlaceholderScreen(title = "Settings", subtitle = "Profile, preferences, and disclaimer")
        }
    }
}
