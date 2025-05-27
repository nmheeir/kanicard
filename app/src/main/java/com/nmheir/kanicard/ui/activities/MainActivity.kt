package com.nmheir.kanicard.ui.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nmheir.kanicard.BuildConfig
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.AppThemeKey
import com.nmheir.kanicard.constants.NavigationBarHeight
import com.nmheir.kanicard.constants.OnboardingCompleteKey
import com.nmheir.kanicard.constants.SearchSource
import com.nmheir.kanicard.constants.SearchSourceKey
import com.nmheir.kanicard.constants.StoragePathKey
import com.nmheir.kanicard.constants.ThemeModeKey
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.core.domain.ui.model.ThemeMode
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.ui.component.DrawerContent
import com.nmheir.kanicard.ui.component.InputFieldHeight
import com.nmheir.kanicard.ui.component.SearchBar
import com.nmheir.kanicard.ui.navigation.navigationBuilder
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.theme.NavigationBarAnimationSpec
import com.nmheir.kanicard.utils.appBarScrollBehavior
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import com.nmheir.kanicard.utils.rememberEnumPreference
import com.nmheir.kanicard.utils.rememberPreference
import com.nmheir.kanicard.utils.resetHeightOffset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: KaniDatabase

    private val onboardingComplete by lazy {
        dataStore[OnboardingCompleteKey] ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        /*        if (!isTaskRoot) {
                    finish()
                    return
                }*/

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        setContent {
            val (storagePath, onStoragePathChange) = rememberPreference(StoragePathKey, "")
            val appTheme by rememberEnumPreference(AppThemeKey, AppTheme.DEFAULT)
            val darkTheme by rememberEnumPreference(ThemeModeKey, ThemeMode.SYSTEM)
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
                if (darkTheme == ThemeMode.SYSTEM) isSystemInDarkTheme else darkTheme == ThemeMode.DARK
            }

            LaunchedEffect(useDarkTheme) {
                setSystemBarAppearance(useDarkTheme)
            }

            KaniTheme(
                darkTheme = useDarkTheme,
                appTheme = appTheme
            ) {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val scope = rememberCoroutineScope()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    val navigationItems = remember { Screens.MainScreen.Screens }
                    val topLevelScreens = listOf(
                        Screens.MainScreen.Home.route,
                        Screens.MainScreen.Statistics.route,
                    )

                    val focusManager = LocalFocusManager.current

                    val windowInsets = WindowInsets.systemBars
                    val density = LocalDensity.current
                    val bottomInset = with(density) { windowInsets.getBottom(density).toDp() }
                    val topInset = with(density) { windowInsets.getTop(density).toDp() }

                    var active by rememberSaveable {
                        mutableStateOf(false)
                    }
                    val (query, onQueryChange) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue())
                    }
                    val onActiveChange: (Boolean) -> Unit = { newActive ->
                        active = newActive
                        if (!newActive) {
                            focusManager.clearFocus()
                            if (backStackEntry?.destination?.route == Screens.MainScreen.Home.route) {
                                onQueryChange(TextFieldValue())
                            }
                        }
                    }


                    var searchSource by rememberEnumPreference(
                        SearchSourceKey,
                        SearchSource.ONLINE
                    )
                    val searchBarFocusRequester = remember { FocusRequester() }
                    val onSearch: (String) -> Unit = {
                        if (it.isNotEmpty()) {
                            onActiveChange(false)
                            navController.navigate("${Screens.Base.Search}/$it")
                            /*if (dataStore[PauseSearchHistoryKey] != true) {
                                database.query {
                                    insert(SearchHistoryEntity(query = it))
                                }
                            }*/
                        }
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

                    val shouldShowSearchBar = remember(backStackEntry, active) {
                        (active
                                || backStackEntry?.destination?.route == Screens.MainScreen.Home.route
                                || backStackEntry?.destination?.route?.startsWith("/search") == true)
                    }

                    /*Scroll Behaviour*/
                    val topAppBarScrollBehavior = appBarScrollBehavior(
                        canScroll = {
                            !(backStackEntry?.destination?.route == Screens.MainScreen.Home.route
                                    || backStackEntry?.destination?.route == Screens.MainScreen.Statistics.route)
                        }
                    )
                    val searchBarScrollBehavior = appBarScrollBehavior(
                        canScroll = {
                            backStackEntry?.destination?.route?.startsWith("${Screens.Base.Search}/") == false
                        }
                    )

                    val localAwareWindowInset =
                        remember(bottomInset, shouldShowNavigationBar, shouldShowSearchBar) {
                            var bottom = bottomInset
                            var top = 0.dp
                            if (shouldShowNavigationBar) bottom += NavigationBarHeight
                            if (shouldShowSearchBar) top += (InputFieldHeight + 4.dp)
                            windowInsets
                                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                                .add(WindowInsets(bottom = bottom, top = top))
                        }

                    LaunchedEffect(active) {
                        if (active) {
                            searchBarScrollBehavior.state.resetHeightOffset()
                        }
                    }

                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = drawerState.isOpen,
                        drawerContent = {
                            DrawerContent(
                                onNavigate = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(it.route)
                                    }
                                }
                            )
                        }
                    ) {
                        CompositionLocalProvider(
                            LocalDatabase provides database,
                            LocalAwareWindowInset provides localAwareWindowInset
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = if (!onboardingComplete) Screens.Base.Onboarding.route else Screens.MainScreen.Home.route,
                                enterTransition = {
                                    if (initialState.destination.route in topLevelScreens
                                        && targetState.destination.route in topLevelScreens
                                    ) {
                                        fadeIn(tween(250))
                                    } else {
                                        fadeIn(tween(250)) + slideInHorizontally { it / 2 }
                                    }
                                },
                                exitTransition = {
                                    if (initialState.destination.route in topLevelScreens
                                        && targetState.destination.route in topLevelScreens
                                    ) {
                                        fadeOut(tween(200))
                                    } else {
                                        fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
                                    }
                                },
                                popEnterTransition = {
                                    if ((initialState.destination.route in topLevelScreens
                                                || initialState.destination.route?.startsWith("${Screens.Base.Search}/") == true)
                                        && targetState.destination.route in topLevelScreens
                                    ) {
                                        fadeIn(tween(250))
                                    } else {
                                        fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
                                    }
                                },
                                popExitTransition = {
                                    if ((initialState.destination.route in topLevelScreens
                                                || initialState.destination.route?.startsWith("${Screens.Base.Search}/") == true)
                                        && targetState.destination.route in topLevelScreens
                                    ) {
                                        fadeOut(tween(200))
                                    } else {
                                        fadeOut(tween(200)) + slideOutHorizontally { it / 2 }
                                    }
                                },
                                modifier = Modifier
                                    .nestedScroll(
                                        if (navigationItems.fastAny { it.route == backStackEntry?.destination?.route } ||
                                            backStackEntry?.destination?.route?.startsWith("${Screens.Base.Search}/") == true) {
                                            searchBarScrollBehavior.nestedScrollConnection
                                        } else {
                                            topAppBarScrollBehavior.nestedScrollConnection
                                        }
                                    )
                            ) {
                                navigationBuilder(
                                    navController = navController,
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
                                            val hideOffset =
                                                (bottomInset + NavigationBarHeight) * (1 - navigationBarHeight / NavigationBarHeight)
                                            IntOffset(x = 0, y = hideOffset.roundToPx())
                                        }
                                    }
                            ) {
                                navigationItems.fastForEach { screens ->
                                    key(screens.route) {
                                        NavigationBarItem(
                                            selected = backStackEntry?.destination?.hierarchy?.any { it.route == screens.route } == true,
                                            icon = {
                                                Icon(
                                                    painter = painterResource(screens.iconRes),
                                                    contentDescription = null
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = stringResource(screens.titleRes),
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

                            AnimatedVisibility(
                                visible = shouldShowSearchBar,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier
                            ) {
                                SearchBar(
                                    query = query,
                                    onQueryChange = onQueryChange,
                                    onSearch = onSearch,
                                    active = active,
                                    onActiveChange = onActiveChange,
                                    scrollBehavior = searchBarScrollBehavior,
                                    placeholder = {
                                        Text(
                                            text = stringResource(
                                                if (!active) R.string.search
                                                else when (searchSource) {
                                                    SearchSource.ONLINE -> R.string.search_online
                                                    SearchSource.LOCAL -> R.string.search_local
                                                }
                                            )
                                        )
                                    },
                                    trailingIcon = {
                                        if (active) {
                                            if (query.text.isNotEmpty()) {
                                                IconButton(
                                                    onClick = { onQueryChange(TextFieldValue("")) }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_close),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    searchSource = searchSource.toggle()
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(
                                                        when (searchSource) {
                                                            SearchSource.ONLINE -> R.drawable.ic_language
                                                            SearchSource.LOCAL -> R.drawable.ic_local_library
                                                        }
                                                    ),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        IconButton(
                                            onClick = {
                                                when {
                                                    active -> onActiveChange(false)
                                                    backStackEntry?.destination?.route != Screens.MainScreen.Home.route -> {
                                                        navController.navigateUp()
                                                    }

                                                    else -> {
                                                        scope.launch {
                                                            drawerState.open()
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                painterResource(
                                                    if (active || !navigationItems.fastAny { it.route == backStackEntry?.destination?.route }) {
                                                        R.drawable.ic_arrow_back
                                                    } else {
                                                        R.drawable.ic_menu
                                                    }
                                                ),
                                                contentDescription = null
                                            )
                                        }
                                    },
                                ) {
                                    if (query.text.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.app_name)
                                        )
                                    } else {
                                        Text(
                                            text = query.text
                                        )
                                    }
                                }
                            }


                            /*AnimatedVisibility(
                            visible = storagePath.isEmpty() || storagePath == "null",
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            PermissionScreen(
                                onPermissionResult = onStoragePathChange
                            )
                        }*/
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setSystemBarAppearance(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView.rootView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor =
                (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor =
                (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
    }
}

val LocalDatabase = staticCompositionLocalOf<KaniDatabase> { error("No database provided") }
val LocalAwareWindowInset =
    staticCompositionLocalOf<WindowInsets> { error("No WindowInset provided") }