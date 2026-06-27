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
import com.mantapp.app.ui.screen.dashboard.DashboardScreen
import com.mantapp.app.ui.screen.dashboard.buildDashboardUiState
import com.mantapp.app.ui.screen.money.IncomeExpenseScreen
import com.mantapp.app.ui.screen.onboarding.OnboardingScreen
import com.mantapp.app.ui.screen.progress.ProgressScreen
import com.mantapp.app.ui.screen.recommendation.RecommendationScreen
import com.mantapp.app.ui.screen.recommendation.buildRecommendationUiState
import com.mantapp.app.ui.state.AuthDestination
import com.mantapp.app.viewmodel.AuthViewModel
import com.mantapp.app.viewmodel.MoneyEntryViewModel
import com.mantapp.app.viewmodel.OnboardingViewModel
import com.mantapp.app.viewmodel.ProgressViewModel

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
                onDashboardClick = {
                    navController.navigate(
                        MantappRoute.Dashboard.createRoute(
                            monthlyIncome = state.monthlyIncome,
                            totalExpenses = state.totalEssentialExpenses,
                            disposableIncome = state.disposableIncome,
                            profileComplete = state.missingInputs.isEmpty(),
                        ),
                    )
                },
                onProgressClick = { navController.navigate(MantappRoute.Progress.route) },
            )
        }
        composable(
            route = MantappRoute.Dashboard.route,
            arguments = listOf(
                navArgument(MantappRoute.Dashboard.MONTHLY_INCOME_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(MantappRoute.Dashboard.TOTAL_EXPENSES_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(MantappRoute.Dashboard.DISPOSABLE_INCOME_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(MantappRoute.Dashboard.PROFILE_COMPLETE_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) { backStackEntry ->
            val arguments = backStackEntry.arguments
            val state = buildDashboardUiState(
                monthlyIncome = arguments?.getString(MantappRoute.Dashboard.MONTHLY_INCOME_ARG),
                totalEssentialExpenses = arguments?.getString(MantappRoute.Dashboard.TOTAL_EXPENSES_ARG),
                disposableIncome = arguments?.getString(MantappRoute.Dashboard.DISPOSABLE_INCOME_ARG),
                profileComplete = arguments?.getBoolean(MantappRoute.Dashboard.PROFILE_COMPLETE_ARG) ?: false,
            )

            DashboardScreen(
                state = state,
                onEnterCashFlowClick = { navController.navigate(MantappRoute.IncomeExpense.route) },
                onTrackProgressClick = { navController.navigate(MantappRoute.Progress.route) },
                onRewardsClick = { navController.navigate(MantappRoute.Rewards.route) },
            )
        }
        composable(MantappRoute.Progress.route) {
            val viewModel = hiltViewModel<ProgressViewModel>()
            val state by viewModel.state.collectAsState()

            ProgressScreen(
                state = state,
                onEvent = viewModel::onEvent,
            )
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
        AuthDestination.Dashboard -> MantappRoute.Dashboard.createRoute(
            monthlyIncome = "",
            totalExpenses = "",
            disposableIncome = "",
            profileComplete = false,
        )
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
