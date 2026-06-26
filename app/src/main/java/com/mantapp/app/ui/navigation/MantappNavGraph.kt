package com.mantapp.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.mantapp.app.ui.screen.PlaceholderScreen
import com.mantapp.app.ui.screen.auth.LoginScreen
import com.mantapp.app.ui.screen.auth.RegistrationScreen
import com.mantapp.app.ui.screen.money.IncomeExpenseScreen
import com.mantapp.app.ui.screen.onboarding.OnboardingScreen
import com.mantapp.app.ui.screen.recommendation.RecommendationScreen
import com.mantapp.app.ui.screen.recommendation.buildRecommendationUiState
import com.mantapp.app.ui.state.AuthDestination
import com.mantapp.app.viewmodel.AuthViewModel
import com.mantapp.app.viewmodel.MoneyEntryViewModel
import com.mantapp.app.viewmodel.OnboardingViewModel

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
            val viewModel = hiltViewModel<AuthViewModel>()
            val state by viewModel.loginState.collectAsState()

            LoginScreen(
                state = state,
                onEvent = viewModel::onLoginEvent,
                onRegisterClick = { navController.navigate(MantappRoute.Register.route) },
                onAuthenticated = { destination -> navController.navigateAuthDestination(destination) },
            )
        }
        composable(MantappRoute.Register.route) {
            val viewModel = hiltViewModel<AuthViewModel>()
            val state by viewModel.registrationState.collectAsState()

            RegistrationScreen(
                state = state,
                onEvent = viewModel::onRegistrationEvent,
                onLoginClick = { navController.popBackStack() },
                onRegistered = { destination -> navController.navigateAuthDestination(destination) },
            )
        }
        composable(MantappRoute.Onboarding.route) {
            val viewModel = hiltViewModel<OnboardingViewModel>()
            val state by viewModel.state.collectAsState()

            OnboardingScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onComplete = {
                    navController.navigate(MantappRoute.IncomeExpense.route) {
                        popUpTo(MantappRoute.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(MantappRoute.IncomeExpense.route) {
            val viewModel = hiltViewModel<MoneyEntryViewModel>()
            val state by viewModel.state.collectAsState()

            IncomeExpenseScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onSaved = { savedState ->
                    navController.navigate(
                        MantappRoute.Recommendation.createRoute(
                            monthlyIncome = savedState.monthlyIncome,
                            totalExpenses = savedState.totalEssentialExpenses,
                            disposableIncome = savedState.disposableIncome,
                        ),
                    )
                },
            )
        }
        composable(
            route = MantappRoute.Recommendation.route,
            arguments = listOf(
                navArgument(MantappRoute.Recommendation.MONTHLY_INCOME_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(MantappRoute.Recommendation.TOTAL_EXPENSES_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(MantappRoute.Recommendation.DISPOSABLE_INCOME_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(MantappRoute.Recommendation.PROFILE_COMPLETE_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) { backStackEntry ->
            val arguments = backStackEntry.arguments
            val state = buildRecommendationUiState(
                monthlyIncome = arguments?.getString(MantappRoute.Recommendation.MONTHLY_INCOME_ARG),
                totalEssentialExpenses = arguments?.getString(MantappRoute.Recommendation.TOTAL_EXPENSES_ARG),
                disposableIncome = arguments?.getString(MantappRoute.Recommendation.DISPOSABLE_INCOME_ARG),
                profileComplete = arguments?.getBoolean(MantappRoute.Recommendation.PROFILE_COMPLETE_ARG) ?: false,
            )

            RecommendationScreen(
                state = state,
                onDashboardClick = { navController.navigate(MantappRoute.Dashboard.route) },
                onProgressClick = { navController.navigate(MantappRoute.Progress.route) },
            )
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

private fun NavHostController.navigateAuthDestination(destination: AuthDestination) {
    val route = when (destination) {
        AuthDestination.Onboarding -> MantappRoute.Onboarding.route
        AuthDestination.Dashboard -> MantappRoute.Dashboard.route
    }

    navigate(
        route = route,
        navOptions = navOptions {
            popUpTo(MantappRoute.Login.route) {
                inclusive = true
            }
            launchSingleTop = true
        },
    )
}
