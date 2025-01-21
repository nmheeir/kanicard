package com.example.kanicard.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kanicard.data.local.KaniDatabase
import com.example.kanicard.ui.component.AppDrawerSheet
import com.example.kanicard.ui.navigation.mainNavigationBuilder
import com.example.kanicard.ui.screen.Screens
import com.example.kanicard.ui.theme.KaniCardTheme
import com.example.kanicard.ui.theme.NavigationBarAnimationSpec
import com.example.kanicard.ui.theme.NavigationBarHeight
import com.example.kanicard.utils.appBarScrollBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: KaniDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KaniCardTheme {

                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                val backStackEntry by navController.currentBackStackEntryAsState()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawerSheet(
                            modifier = Modifier,
                            onNavigate = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(it)
                            }
                        )
                    },
                    modifier = Modifier
                        .safeDrawingPadding()
                        .windowInsetsPadding(WindowInsets.navigationBars),
//                    gesturesEnabled = false
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        val width = maxWidth
                        val navigationItems = remember { Screens.MainScreens }
                        val topLevelScreens = listOf(
                            Screens.Home.route,
                            Screens.Statistics.route,
                        )

                        val windowInsets = WindowInsets.systemBars
                        val density = LocalDensity.current
                        val bottomInset = with(density) { windowInsets.getBottom(density).toDp() }

                        var active by rememberSaveable {
                            mutableStateOf(false)
                        }

                        val shouldShowNavigationBar = remember(backStackEntry, active) {
                            backStackEntry?.destination?.route == null
                                    || navigationItems.fastAny { it.route == backStackEntry?.destination?.route }
                                    && !active
                        }

                        val navigationBarHeight by animateDpAsState(
                            targetValue = if (shouldShowNavigationBar) NavigationBarHeight else 0.dp,
                            label = "",
                            animationSpec = NavigationBarAnimationSpec
                        )

                        val appBarScrollBehavior = appBarScrollBehavior(
                            canScroll = {
                                backStackEntry?.destination?.route == Screens.Home.route
                                        || backStackEntry?.destination?.route == Screens.Statistics.route
                            }
                        )

                        val topAppBarScrollBehavior = appBarScrollBehavior(
                            canScroll = {
                                !(backStackEntry?.destination?.route == Screens.Home.route
                                        || backStackEntry?.destination?.route == Screens.Statistics.route)
                            }
                        )

                        Log.d("mainactivity", "onCreate: $navigationItems")
                        CompositionLocalProvider(
                            // TODO: Chỗ này provide sau (tùy vào nhu cầu)
                            LocalDatabase provides database
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screens.Home.route
                            ) {
                                mainNavigationBuilder(
                                    navController = navController,
                                    appBarScrollBehavior = appBarScrollBehavior,
                                    topAppBarScrollBehavior = topAppBarScrollBehavior
                                )
                            }

                            NavigationBar(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset {
                                        if (navigationBarHeight == 0.dp) {
                                            IntOffset(
                                                x = 0,
                                                y = (bottomInset + NavigationBarHeight).roundToPx()
                                            )
                                        } else {
                                            IntOffset(x = 0, y = 0)
                                        }
                                    }
                            ) {
                                navigationItems.fastForEach { screens ->
                                    key(screens.route) {
                                        NavigationBarItem(
                                            selected = backStackEntry?.destination?.hierarchy?.any { it.route == screens.route } == true,
                                            icon = {
                                                Icon(
                                                    painter = painterResource(screens.iconId),
                                                    contentDescription = null
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = stringResource(screens.titleId),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            onClick = {
                                                if (backStackEntry?.destination?.hierarchy?.any { it.route == screens.route } == true) {
                                                    backStackEntry?.savedStateHandle?.set(
                                                        "scrollToTop",
                                                        true
                                                    )
                                                } else {
                                                    navController.navigate(screens.route) {
                                                        popUpTo(navController.graph.startDestinationId) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

val LocalDatabase = staticCompositionLocalOf<KaniDatabase> { error("No database provided") }