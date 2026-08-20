package com.finly.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finly.app.security.BiometricAuthHelper
import com.finly.app.ui.splash.SplashScreen
import com.finly.core.domain.repository.UserPreferencesRepository
import com.finly.core.ui.theme.CardNavy
import com.finly.core.ui.theme.DeepNavy
import com.finly.core.ui.theme.MoneyMindTheme
import com.finly.core.ui.theme.PrimaryIndigo
import com.finly.core.ui.theme.ScoreExcellent
import com.finly.core.ui.theme.TextMutedDark
import com.finly.core.ui.theme.TextPrimaryDark
import com.finly.core.ui.theme.TextSecondaryDark
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

sealed class NavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : NavScreen("splash", "Splash", Icons.Rounded.Home)
    object Home : NavScreen("home", "Home", Icons.Rounded.Home)
    object Insights : NavScreen("insights", "Insights", Icons.Rounded.PieChart)
    object Goals : NavScreen("goals", "Goals", Icons.Rounded.Flag)
    object Coach : NavScreen("coach", "Coach", Icons.Rounded.AutoAwesome)
    object Profile : NavScreen("profile", "Profile Data", Icons.Rounded.AccountBalance)

    object Onboarding : NavScreen("onboarding", "Onboarding", Icons.Rounded.Home)
    object TransactionReview : NavScreen("transaction_review", "Review", Icons.Rounded.PieChart)
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyMindTheme {
                MoneyMindAppMainScreen(
                    userPreferencesRepository = userPreferencesRepository,
                    activity = this
                )
            }
        }
    }
}

@Composable
fun MoneyMindAppMainScreen(
    userPreferencesRepository: UserPreferencesRepository,
    activity: FragmentActivity
) {
    var isUnlocked by remember { mutableStateOf(!userPreferencesRepository.isBiometricEnabled()) }

    LaunchedEffect(userPreferencesRepository.isBiometricEnabled()) {
        if (userPreferencesRepository.isBiometricEnabled() && !isUnlocked) {
            BiometricAuthHelper.promptBiometric(
                activity = activity,
                title = "Unlock MoneyMind AI",
                subtitle = "Verify fingerprint or PIN to access vault",
                onSuccess = { isUnlocked = true },
                onError = { }
            )
        }
    }

    if (!isUnlocked) {
        // Biometric Lock Screen Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavy),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fingerprint,
                    contentDescription = "Lock",
                    tint = ScoreExcellent,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "MoneyMind Vault Locked",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Authenticate via fingerprint or device PIN to access your encrypted financial data.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        BiometricAuthHelper.promptBiometric(
                            activity = activity,
                            title = "Unlock MoneyMind AI",
                            subtitle = "Verify fingerprint or PIN to access vault",
                            onSuccess = { isUnlocked = true },
                            onError = { }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Unlock MoneyMind AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        NavScreen.Home,
        NavScreen.Insights,
        NavScreen.Goals,
        NavScreen.Coach
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CardNavy,
                    contentColor = TextPrimaryDark
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, maxLines = 1) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = PrimaryIndigo,
                                unselectedIconColor = TextMutedDark,
                                unselectedTextColor = TextMutedDark
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = DeepNavy
        ) {
            NavHost(
                navController = navController,
                startDestination = NavScreen.Splash.route
            ) {
                composable(NavScreen.Splash.route) {
                    SplashScreen(
                        onSplashFinished = {
                            val startTarget = if (userPreferencesRepository.isOnboardingCompleted()) {
                                NavScreen.Home.route
                            } else {
                                NavScreen.Onboarding.route
                            }
                            navController.navigate(startTarget) {
                                popUpTo(NavScreen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(NavScreen.Home.route) {
                    com.finly.feature.home.HomeScreen(
                        onNavigateToInsights = { navController.navigate(NavScreen.Insights.route) },
                        onNavigateToCoach = { navController.navigate(NavScreen.Coach.route) },
                        onNavigateToTransactions = { navController.navigate(NavScreen.TransactionReview.route) },
                        onNavigateToProfile = { navController.navigate(NavScreen.Profile.route) }
                    )
                }
                composable(NavScreen.Insights.route) {
                    com.finly.feature.insights.InsightsScreen(
                        onNavigateToProfile = { navController.navigate(NavScreen.Profile.route) }
                    )
                }
                composable(NavScreen.Goals.route) {
                    com.finly.feature.goals.GoalsScreen(
                        onNavigateToProfile = { navController.navigate(NavScreen.Profile.route) }
                    )
                }
                composable(NavScreen.Coach.route) {
                    com.finly.feature.coach.CoachScreen(
                        onNavigateToProfile = { navController.navigate(NavScreen.Profile.route) }
                    )
                }
                composable(NavScreen.Profile.route) {
                    com.finly.feature.profile.ProfileScreen(
                        onStartOnboarding = { navController.navigate(NavScreen.Onboarding.route) }
                    )
                }
                composable(NavScreen.Onboarding.route) {
                    com.finly.app.ui.onboarding.OnboardingScreen(
                        onOnboardingComplete = {
                            navController.navigate(NavScreen.Home.route) {
                                popUpTo(NavScreen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(NavScreen.TransactionReview.route) {
                    com.finly.app.ui.review.TransactionReviewScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
